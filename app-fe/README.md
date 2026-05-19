# Royalty Progress (Next.js + TypeScript)

A Next.js application that displays a paginated table of 100 songs with royalty calculation progress.

The data lives on the **backend Scala service**. The FE fetches it from `${BACKEND_URL}/songs?page=N&pageSize=N` (default `http://localhost:8080`).

## Prerequisites

- Node.js 18.18+ and npm 10+
- A running backend on `http://localhost:8080` (see [`../app-api/README.md`](../app-api/README.md)).

## Run locally

```bash
cd app-fe
npm install
npm run dev
```

Open `http://localhost:3000` in your browser.

To point at a non-local backend, set the environment variable before starting:

```bash
BACKEND_URL=https://commission-api.example.com npm run dev
```

## Scripts

```bash
npm run dev           # development server on :3000
npm run build         # production build
npm start             # run the production build
npm run lint          # ESLint
npm run format        # Prettier write
npm run typecheck     # tsc --noEmit (strict TypeScript)
npm test              # Jest unit + component tests (14 tests)
npm run test:coverage # coverage report
```

## Architectural decisions

- Next.js App Router + TypeScript strict. Per the assignment. App Router is the canonical Next.js path now and gives us server components, route handlers, and streaming if/when we need them.
- Material UI v6 + AppRouterCacheProvider. Avoids rolling a table + pagination from scratch.
- Layered services (`HttpService` → `SongService` → `PaginationService`).
- Generic `Page<T>` + reusable `PaginationBar`.
- CSP `connect-src *` for dev. Allows the cross-origin fetch to the BE. Production should narrow to the actual BE host(s).

## URL

| URL                      | Description                                               |
| ------------------------ | --------------------------------------------------------- |
| `http://localhost:3000/` | Songs table — pagination updates via AJAX, no page reload |
