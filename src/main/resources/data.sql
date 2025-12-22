INSERT INTO classroom (name) VALUES ('A반');
INSERT INTO app_user (name, role) VALUES
('박선생', 'TEACHER'),
('이선생', 'TEACHER'),
('최선생', 'TEACHER'),
('정선생', 'TEACHER'),
('관리자', 'ADMIN');

INSERT INTO task_template (title, description, condition_tag, category, active) VALUES
('마스크 착용 권고', '미세먼지 나쁨 이상 시 마스크 착용 안내', 'PM_BAD', 'ENVIRONMENT', true),
('공기청정기 가동', '미세먼지 나쁨 이상 시 공기청정기/환기 조절', 'PM_BAD', 'ENVIRONMENT', true),
('미세먼지 매우나쁨 대응', '미세먼지 매우나쁨이면 외부활동 전면 금지', 'PM_VERY_BAD', 'ENVIRONMENT', true),

('독감 의심 증상 체크', '독감 주의/경보 시 발열·기침 등 증상 체크', 'FLU_WARNING', 'HEALTH', true),
('손 씻기 스티커 교육', '독감 경보 시 손 씻기 지도 강화', 'FLU_ALERT', 'HEALTH', true),
('교실 소독 강화', '독감 경보 시 장난감/손잡이 소독 강화', 'FLU_ALERT', 'HEALTH', true),

('우산 정리/건조', '비 오는 날 우산 정리 및 바닥 미끄럼 주의', 'RAINY', 'SAFETY', true),
('실내 미끄럼 방지', '비 오는 날 매트 점검 및 미끄럼 사고 예방', 'RAINY', 'SAFETY', true),

('눈길 안전 지도', '눈 오는 날 등하원 안전 지도 및 미끄럼 주의', 'SNOWY', 'SAFETY', true),
('난방 점검', '추운 날 난방 상태 및 실내온도 점검', 'COLD', 'FACILITY', true),
('온열/수분 보충', '더운 날 물 자주 마시기 및 열사병 예방', 'HOT', 'HEALTH', true),

('놀이감 소독(일상)', '일일 장난감 소독 루틴', 'DAILY', 'HEALTH', true),
('출석/건강 체크', '등원 시 컨디션/체온 체크', 'DAILY', 'HEALTH', true)
ON DUPLICATE KEY UPDATE
  description = VALUES(description),
  active = VALUES(active),
  category = VALUES(category);