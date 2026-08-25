CREATE TABLE test_results (
  id BIGSERIAL PRIMARY KEY,
  owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  test_name VARCHAR(100) NOT NULL,
  status VARCHAR(16) NOT NULL,
  score NUMERIC(8,2) NOT NULL CHECK (score >= 0 AND score <= 100),
  tested_at TIMESTAMP WITH TIME ZONE NOT NULL,
  notes VARCHAR(1000),
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  version BIGINT
);
CREATE INDEX idx_test_results_owner_tested_at ON test_results(owner_id, tested_at DESC);
