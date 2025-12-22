package com.example.kindergarten.application.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AirResponse {

    @JsonProperty("hourly")
    public Hourly hourly;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hourly {
        public List<String> time;
        public List<Double> pm10;

        @JsonProperty("pm2_5")
        public List<Double> pm2_5;
    }
}
