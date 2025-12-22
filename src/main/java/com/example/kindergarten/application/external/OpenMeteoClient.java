package com.example.kindergarten.application.external;

import com.example.kindergarten.domain.env.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenMeteoClient {

    private final RestClient restClient = RestClient.create();

    @Value("${external.open-meteo.forecast-url:https://api.open-meteo.com/v1/forecast}")
    private String forecastUrl;

    @Value("${external.open-meteo.archive-url:https://archive-api.open-meteo.com/v1/archive}")
    private String archiveUrl;

    @Value("${external.open-meteo.air-url:https://air-quality-api.open-meteo.com/v1/air-quality}")
    private String airUrl;

    @Value("${external.open-meteo.latitude:37.5665}")
    private double latitude;

    @Value("${external.open-meteo.longitude:126.9780}")
    private double longitude;

    @Value("${external.open-meteo.timezone:Asia/Seoul}")
    private String timezone;

    public AutoEnvData fetchAutoEnvData(LocalDate date) {
        return fetchAutoEnvData(date, null, null);
    }

    public AutoEnvData fetchAutoEnvData(LocalDate date, Double lat, Double lon) {
        double usedLat = (lat != null) ? lat : latitude;
        double usedLon = (lon != null) ? lon : longitude;

        boolean past = date.isBefore(LocalDate.now());
        String weatherBaseUrl = past ? archiveUrl : forecastUrl;

        // Weather
        log.info("[External] request weather: baseUrl={}, date={}, lat={}, lon={}, tz={}",
                weatherBaseUrl, date, usedLat, usedLon, timezone);

        URI weatherUri = UriComponentsBuilder
                .fromHttpUrl(weatherBaseUrl)
                .queryParam("latitude", usedLat)
                .queryParam("longitude", usedLon)
                .queryParam("timezone", timezone)
                .queryParam("start_date", date)
                .queryParam("end_date", date)
                .queryParam("daily", "temperature_2m_max,temperature_2m_min,weather_code")
                .build(true)
                .toUri();

        log.info("[External] weatherUri={}", weatherUri);

        WeatherResponse weather = restClient.get()
                .uri(weatherUri)
                .retrieve()
                .body(WeatherResponse.class);

        if (weather == null || weather.daily == null || weather.daily.time == null || weather.daily.time.isEmpty()) {
            throw new IllegalStateException("Open-Meteo weather 데이터 없음: " + date);
        }

        double tMax = safeGet(weather.daily.temperatureMax, 0, 0.0);
        double tMin = safeGet(weather.daily.temperatureMin, 0, 0.0);
        int weatherCode = safeGetInt(weather.daily.weatherCode, 0, 0);

        int tempInt = (int) Math.round((tMax + tMin) / 2.0);
        WeatherType weatherType = mapWeatherCode(weatherCode);

        // Air
        log.info("[External] request air: baseUrl={}, date={}, lat={}, lon={}, tz={}",
                airUrl, date, usedLat, usedLon, timezone);

        URI airUri = UriComponentsBuilder
                .fromHttpUrl(airUrl)
                .queryParam("latitude", usedLat)
                .queryParam("longitude", usedLon)
                .queryParam("timezone", timezone)
                .queryParam("start_date", date)
                .queryParam("end_date", date)
                .queryParam("hourly", "pm10,pm2_5")
                .build(true)
                .toUri();


        log.info("[External] airUri={}", airUri);

        AirResponse air = restClient.get()
                .uri(airUri)
                .retrieve()
                .toEntity(AirResponse.class)
                .getBody();

        Double pm10RawMax = maxOf(air != null && air.hourly != null ? air.hourly.pm10 : null);
        Double pm25RawMax = maxOf(air != null && air.hourly != null ? air.hourly.pm2_5 : null);

        PmLevel pm10Level = mapPm10(pm10RawMax);
        PmLevel pm25Level = mapPm25(pm25RawMax);

        log.info("[External] mapped(auto): date={}, weatherCode={}, weatherType={}, temp={}C, pm10Max={}, pm25Max={}, pm10Level={}, pm25Level={}",
                date, weatherCode, weatherType, tempInt, pm10RawMax, pm25RawMax, pm10Level, pm25Level);

        return new AutoEnvData(date, weatherType, tempInt, pm10Level, pm25Level);
    }

    private static Double maxOf(List<Double> list) {
        if (list == null || list.isEmpty()) return null;
        return list.stream().filter(v -> v != null).max(Comparator.naturalOrder()).orElse(null);
    }

    private static double safeGet(List<Double> list, int idx, double defaultValue) {
        if (list == null || list.size() <= idx || list.get(idx) == null) return defaultValue;
        return list.get(idx);
    }

    private static int safeGetInt(List<Integer> list, int idx, int defaultValue) {
        if (list == null || list.size() <= idx || list.get(idx) == null) return defaultValue;
        return list.get(idx);
    }

    private WeatherType mapWeatherCode(int code) {
        if (code == 0) return WeatherType.SUNNY;
        if (code == 1 || code == 2 || code == 3) return WeatherType.CLOUDY;
        if ((code >= 51 && code <= 67) || (code >= 80 && code <= 82) || (code >= 61 && code <= 65)) return WeatherType.RAINY;
        if ((code >= 71 && code <= 77) || (code >= 85 && code <= 86)) return WeatherType.SNOWY;
        return WeatherType.UNKNOWN;
    }

    private PmLevel mapPm10(Double v) {
        if (v == null) return null;
        if (v <= 30) return PmLevel.GOOD;
        if (v <= 80) return PmLevel.NORMAL;
        if (v <= 150) return PmLevel.BAD;
        return PmLevel.VERY_BAD;
    }

    private PmLevel mapPm25(Double v) {
        if (v == null) return null;
        if (v <= 15) return PmLevel.GOOD;
        if (v <= 35) return PmLevel.NORMAL;
        if (v <= 75) return PmLevel.BAD;
        return PmLevel.VERY_BAD;
    }

    // 응답 DTO
    public static class WeatherResponse {
        public Daily daily;

        public static class Daily {
            public List<String> time;

            @JsonProperty("temperature_2m_max")
            public List<Double> temperatureMax;

            @JsonProperty("temperature_2m_min")
            public List<Double> temperatureMin;

            @JsonProperty("weather_code")
            public List<Integer> weatherCode;
        }
    }

    public static class AirQualityResponse {
        public Hourly hourly;

        public static class Hourly {
            public List<Double> pm10;

            @JsonProperty("pm2_5")
            public List<Double> pm2_5;
        }
    }

    // 서비스에 넘길 결과
    public record AutoEnvData(
            LocalDate date,
            WeatherType weatherType,
            Integer temperature,
            PmLevel pm10Level,
            PmLevel pm25Level
    ) {}
}
