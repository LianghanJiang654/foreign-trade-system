# Foreign Trade System

A backend system for managing international trade orders — clients, products,
orders, and order line items — built as a portfolio project demonstrating
Java/Spring Boot backend development with a relational database.

## What it does

- **Client management** — track overseas buyers, their country, contact
  details, and settlement currency
- **Product catalog** — manage products with SKU, pricing, and stock levels
- **Order management** — create orders linked to a client, track status
  (quoting / confirmed / shipped / completed) and trade term (e.g. FOB, CIF)
- **Order line items** — record which products (and quantities) are part of
  each order, with the **unit price captured at the time of the order**
  rather than looked up live from the product catalog — so historical orders
  stay accurate even after prices change later

## Tech stack

- **Backend:** Java 21, Spring Boot 4, Spring Data JPA (Hibernate)
- **Database:** MySQL 8 (hosted on Aiven)
- **Build tool:** Maven

## Project structure
## Database schema

Four tables, with foreign key relationships:

- `client` — id, company_name, country, contact_name, email, currency, created_at
- `product` — id, sku (unique), name, unit_price, stock_quantity, created_at
- `orders` — id, client_id (FK → client), status, trade_term, total_amount, created_at
- `order_item` — id, order_id (FK → orders), product_id (FK → product), quantity, unit_price

## Setup

1. Clone the repo
2. Copy the example config and fill in your own database credentials:
```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
```
3. Edit `application.properties` with your MySQL connection details (host, port,
   username, password)
4. Run the four `CREATE TABLE` statements (see Database schema above) against
   your MySQL instance before starting the app — this project uses
   `spring.jpa.hibernate.ddl-auto=validate`, so it expects the tables to
   already exist and match the entity definitions exactly
5. Run the application:
```bash
   ./mvnw spring-boot:run
```
6. The API will be available at `http://localhost:8080`

## API endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | /api/clients | List all clients |
| POST | /api/clients | Create a client |
| GET | /api/product | List all products |
| POST | /api/product | Create a product |
| GET | /api/orders | List all orders |
| POST | /api/orders | Create an order (pass `client: { "id": <id> }` to link a client) |
| GET | /api/order-items | List all order line items |
| POST | /api/order-items | Create an order line item (pass `order: { "id": <id> }` and `product: { "id": <id> }`) |

## A design decision worth noting

`OrderItem.unitPrice` is stored separately from `Product.unitPrice` on
purpose. `Product.unitPrice` reflects the **current** selling price, while
`OrderItem.unitPrice` is a **snapshot** of the price at the moment the order
was placed. If a product's price changes later, past orders should still
show what the customer actually paid — this is a common pattern in
real-world order/inventory systems, not just a duplicate field.

## Possible next steps

- Add a Service layer between Controller and Repository for business logic
  (e.g. stock validation, order status transitions)
- Add DTOs so API responses don't expose partially-null nested entities
  when only an id is passed in a relationship
- Add a simple frontend to visualize clients, products, and orders
- Deploy the backend (e.g. Render) and connect it to the hosted MySQL instance