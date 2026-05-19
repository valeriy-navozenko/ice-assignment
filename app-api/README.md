# Commission API

A small Scala HTTP API that calculates commissions for a list of services using a rate table.

## What it does

For each request `(clientId, [(id, amount), ...])`, the API returns a commission per item using a rate table:

| Range | Rate |
| --- | --- |
| 0 – 1,000 | 10% |
| 1,000 – 3,000 | 5% |
| 3,000 – 1,000,000 | 1% |

Example: `(1) 900`, `(2) 2000`, `(3) 4000` → commissions `90`, `100`, `40`, total `230`.

## Run locally

```bash
cd app-api
sbt run
```

The server listens on `http://localhost:8080`.

## Try it

```bash
curl -s localhost:8080/health
# {"status":"ok"}

curl -s -X POST localhost:8080/commissions \
  -H 'Content-Type: application/json' \
  -d '{"clientId":"client-001","services":[
        {"id":1,"amount":900},
        {"id":2,"amount":2000},
        {"id":3,"amount":4000}]}'

curl -s 'localhost:8080/songs?page=1&pageSize=50'
```

## Tests, format

```bash
sbt test            # unit tests
sbt scalafmtAll     # format the codebase
```


## Endpoints

| Method | Path | What |
| --- | --- | --- |
| GET | `/health` | Liveness probe |
| POST | `/commissions` | Calculate commissions for a client |
| GET | `/songs?page&pageSize` | Paginated royalty-progress songs |

Error responses use a small JSON shape: `{ "code": "...", "error": "..." }`.

| HTTP | `code` | When |
| --- | --- | --- |
| 400 | `invalid_request` | Malformed JSON, blank clientId, empty list, duplicate ids, non-positive service id |
| 422 | `amount_out_of_range` | Amount < 0 or > 1,000,000 |
| 422 | `no_matching_tier` | Rate table does not span the amount |

## Configuration

All optional, fall back to development defaults:

| Env var | Default |
| --- | --- |
| `APP_HOST` | `0.0.0.0` |
| `APP_PORT` | `8080` |

## Architectural decisions

- Stack: Scala 2.13.12 + sbt 1.9.6, http4s, cats-effect, circe, weaver-cats. Cats stack came with the scaffold; http4s + circe added on top.
- Layers: `domain/`, `service/` , `api/`, `config/`.
- No DTO layer. The domain case classes are the wire shape; circe decoders/encoders.
- No commission cache. The math is nanoseconds — caching pure CPU was overhead, not a win.
- CORS allowed from any origin so the browser FE on `:3000` can call the BE on `:8080`.
