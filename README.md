# Foreign Trade System

A backend system for managing international trade orders — clients, products,
orders, and order line items — built as a portfolio project to demonstrate
production-grade Java backend development, not just basic CRUD.

🔗 **Live Demo**: https://foreign-trade-system.onrender.com
(Free-tier instance sleeps after inactivity — first request may take 30-60s to wake up)

📄 **API Documentation**: `/swagger-ui.html` on the running instance — auto-generated
from the codebase, always in sync with the actual API (no hand-maintained docs to go stale)

## Why this project exists

Most portfolio CRUD projects stop at "create a record, read a record." This one
goes further — it's built around a real business problem (overselling stock
under concurrent load) and solves it the way a production system would: with
optimistic locking, transactional integrity, and a Redis-backed flash-sale
flow, not just a happy-path demo.

## Tech stack

- **Backend:** Java 21, Spring Boot 4, Spring Data JPA (Hibernate)
- **Database:** MySQL 8 (hosted on Aiven)
- **Cache / high-concurrency layer:** Redis-compatible Valkey (hosted on Aiven)
- **Testing:** JUnit 5 + Mockito
- **API docs:** springdoc-openapi (Swagger UI)
- **Containerization:** Docker (multi-stage build)
- **Deployment:** Render

## What it does

- **Client management** — track overseas buyers, their country, contact
  details, and settlement currency
- **Product catalog** — manage products with SKU, pricing, and stock levels
- **Order management** — create orders linked to a client, track status
  (quoting / confirmed / shipped / completed) and trade term (e.g. FOB, CIF)
- **Order line items** — record which products (and quantities) are part of
  each order, with the **unit price captured at the time of the order**
  rather than looked up live from the product catalog
- **Cached product lookups** — single-product reads go through a Redis
  cache-aside layer before hitting MySQL
- **Flash-sale endpoint** — a dedicated seckill flow that pre-deducts stock
  atomically in Redis, so a burst of concurrent requests never overwhelms
  the database

## Architecture
Controllers only handle HTTP concerns (request/response, status codes).
All business rules — stock validation, concurrency handling, transaction
boundaries — live in the Service layer. This isn't just "clean code" for
its own sake: it's what makes the logic unit-testable without spinning up
a real database (see Testing below).

## Database schema

Four tables, with foreign key relationships:

- `client` — id, company_name, country, contact_name, email, currency, created_at
- `product` — id, sku (unique), name, unit_price, stock_quantity, **version** (optimistic lock), created_at
- `orders` — id, client_id (FK → client), status, trade_term, total_amount, created_at
- `order_item` — id, order_id (FK → orders), product_id (FK → product), quantity, unit_price

## Design decisions worth explaining

### Why `order_item.unit_price` is stored separately from `product.unit_price`

`product.unit_price` reflects the **current** selling price; `order_item.unit_price`
is a **snapshot** of the price at the moment the order was placed — like a
receipt. If a product's price changes later, historical orders still show
what the customer actually paid. Without this, querying an old order would
silently show today's price instead of the price the customer agreed to.

### Why optimistic locking (`@Version`) instead of `synchronized`

Two concurrent requests can both read "stock = 5", both decide it's enough,
and both deduct — overselling the item. `synchronized` would prevent this,
but only within a single JVM process; it does nothing once the app is
horizontally scaled across multiple instances. Optimistic locking (a
`version` column checked on every update) works correctly regardless of
how many instances are running, because the conflict is detected at the
database level, not the application level. On a version mismatch, the
save fails with `OptimisticLockException`, which is caught and converted
into a clear "please retry" response instead of a raw 500.

### Why `@Transactional` on order creation

Creating an order line item involves two separate writes: decrementing
stock, then inserting the line item. Without a transaction boundary, a
failure between those two writes would leave stock decremented with no
corresponding order record — a silent data inconsistency. `@Transactional`
ensures both writes succeed together or roll back together.

### Why the flash-sale endpoint doesn't rely on the same optimistic-lock path

Optimistic locking works well under normal load, but under a burst of
simultaneous requests for the same limited-stock item (a flash sale), most
requests would fail on version conflicts and need to retry — a poor user
experience and unnecessary database load. The `/api/seckill` endpoint
instead pre-loads stock into Redis and uses `DECR`, which Redis executes
as a single-threaded atomic operation — no race condition is possible,
and requests that would fail get rejected immediately without touching
MySQL at all. Only requests that actually "win" stock go on to hit the
database. **Known limitation:** the decrement-then-check-then-increment-back
logic on failure isn't wrapped in a single atomic operation; a process
crash between those steps could leave Redis stock in an inconsistent
state. A production version of this would move that logic into a Lua
script executed atomically on the Redis server.

### Why errors return a consistent JSON shape

All exceptions are caught by a single `@RestControllerAdvice` class and
converted into `{ code, message, timestamp }`, instead of each controller
handling its own errors differently. Callers can parse every error
response the same way, and adding a new endpoint doesn't mean re-writing
error handling for it.

## Testing

Service-layer business logic (stock validation, the success and
insufficient-stock paths) is covered by unit tests using JUnit 5 and
Mockito — repositories are mocked, so tests run in milliseconds without
touching a real database. This is deliberately separate from manual
integration testing via Postman, which exercises the full stack including
the live MySQL/Redis instances.

## Setup

1. Clone the repo
2. Copy the example config and fill in your own credentials:
```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
```
3. You'll need a MySQL instance and a Redis/Valkey instance (both have free
   tiers on Aiven). Set these as environment variables before running:
    - `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
    - `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
4. Run the four `CREATE TABLE` statements (see Database schema above) against
   your MySQL instance before starting the app — this project uses
   `spring.jpa.hibernate.ddl-auto=validate`, so it expects the tables to
   already exist and match the entity definitions exactly
5. Run the application:
```bash
   ./mvnw spring-boot:run
```
6. API available at `http://localhost:8080`, interactive docs at
   `http://localhost:8080/swagger-ui.html`

## API endpoints

Full interactive documentation is available via Swagger UI (see above) —
it's generated from the code, so it's always accurate. Summary:

| Method | Endpoint | Description |
|---|---|---|
| GET/POST | /api/clients | List / create clients |
| GET/POST | /api/product | List / create products |
| GET | /api/product/{id} | Get one product (cached) |
| POST | /api/product/{id}/sync-stock | Sync a product's stock into Redis for seckill |
| GET/POST | /api/orders | List / create orders |
| GET/POST | /api/order-items | List / create order line items (with stock validation) |
| POST | /api/seckill/{productId}?quantity=N | Attempt to claim stock via the flash-sale flow |

## Possible next steps

- Move the seckill increment-on-failure logic into a Lua script for full atomicity
- Add rate limiting on the seckill endpoint
- Add DTOs so API responses don't expose partially-null nested entities
  when only an id is passed in a relationship
- Add a simple frontend to visualize clients, products, and orders