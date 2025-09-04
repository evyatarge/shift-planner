# Backend (Java 21, Spring Boot, OptaPlanner)

## Run
```bash
cd server
mvn spring-boot:run
```
Server listens on `http://localhost:8080`.

## Feature flags
- `feature.availability.enabled=false` (default) — availability constraint is **disabled** by setting configurable weight to ZERO.
- Set `true` to **enable** availability hard constraint.

## API
`POST /api/solve`
- Request: employees[], tasks[], optional availabilities[]
- Response: assignments[], score, unassignedCount

See root README for examples.
