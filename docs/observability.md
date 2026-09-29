# Observability

This project exposes standard Spring Boot Actuator endpoints and a Prometheus metrics endpoint.

- Actuator base: `/actuator`
- Health: `/actuator/health` (shows details when authorized)
- Info: `/actuator/info`
- Prometheus metrics: `/actuator/prometheus`
- Other useful endpoints: `/actuator/metrics`, `/actuator/loggers`, `/actuator/threaddump`

Prometheus scraping example (prometheus.yml):

```yaml
scrape_configs:
  - job_name: 'anyvet-boilerplate'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['<host>:8080']
```

The project uses a JSON log encoder by default (Logstash Logback Encoder) and exposes structured logs to the console for ingestion by a log aggregator.

