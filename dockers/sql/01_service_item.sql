-- 서비스 항목 테이블 (백엔드 ServiceItem 과 같은 필드)
-- IF NOT EXISTS: 이미 테이블이 있으면 에러 없이 건너뜀 (여러 번 실행해도 안전)
CREATE TABLE IF NOT EXISTS service_item (
    -- 번호. DB가 1, 2, 3... 자동으로 매김
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    -- 서비스명. 비워 둘 수 없음
    name      VARCHAR(100) NOT NULL,
    -- 카테고리
    category  VARCHAR(50)  NOT NULL,
    -- 가격. 0보다 커야 함
    price     INTEGER      NOT NULL CHECK (price > 0)
);

-- 처음 데이터 3개 (테이블이 비어 있을 때만 넣음)
INSERT INTO service_item (name, category, price)
SELECT * FROM (VALUES
    ('에어컨 청소', '가전 청소', 50000),
    ('욕실 청소',   '청소',      80000),
    ('이사 청소',   '청소',      90000)
) AS seed(name, category, price)
WHERE NOT EXISTS (SELECT 1 FROM service_item);