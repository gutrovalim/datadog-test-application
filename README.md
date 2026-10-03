# datadog-test-application

Two small Spring Boot services (Java 21) instrumented with the Datadog Java tracer, for hands-on APM practice.

```
client ──> orders-service :8080 ──HTTP──> inventory-service :8081
                 │
                 └──JDBC──> Postgres
```

Each `POST /orders` produces one distributed trace: Spring MVC → custom `order.place` span → HTTP call to inventory → JDBC insert.
`inventory-service` adds random latency (50–350ms) and ~5% random 500s; `OUT-OF-STOCK` gives 409, unknown SKUs 404.

## Run

1. Get a Datadog API key (Organization Settings → API Keys).
2. `cp .env.example .env` and fill in `DD_API_KEY` (and `DD_SITE` if not US1, e.g. `datadoghq.eu`).
3. `docker compose up --build` (needs Docker running; no local JDK required).
4. `./traffic.sh` to generate requests.
5. In Datadog: APM → Services / Traces, filter `env:dev`.

Things to try: open a trace flame graph, find an error trace, compare `orders-service` latency vs. its inventory dependency, view the Service Map.
