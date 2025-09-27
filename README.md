# Angular Travel System

## 🚀 Project Highlights
- Modern travel booking app (Angular + Spring Boot)
- Secure checkout with order tracking
- Responsive UI (Angular Material)
- RESTful backend, MySQL database
- Docker-ready for cloud deployment

## 🛠️ Tech Stack
![Angular](https://img.shields.io/badge/Angular-14-red?logo=angular)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-green?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8-blue?logo=mysql)
![Docker](https://img.shields.io/badge/Docker-ready-blue?logo=docker)

## 📸 Screenshots
<!-- Add your screenshots to the repo and update these paths -->
![alt text](image-1.png)

Full-stack travel shopping app. The frontend (Angular) lets users browse vacations and excursions, manage a cart, and place orders. The backend (Spring Boot + MySQL) exposes REST APIs, persists data, and enforces referential integrity with cascading deletes.

## Tech Stack
- Frontend: Angular 14, Angular Material, Angular CDK, Flex Layout, RxJS, TypeScript
- Backend: Spring Boot 3, Spring Data JPA, Spring Data REST, Bean Validation (Jakarta Validation)
- Database: MySQL 8 (InnoDB), SQL schema + seed scripts
- Build/Tooling: Maven, Node.js + npm, Angular CLI, TypeScript, Karma/Jasmine

## Project Structure
- `client/` – Angular app (UI, services, models)
- `demo/` – Spring Boot app (REST controllers, JPA entities, services)

## Backend Structure (prioritized)
- `demo/src/main/resources/application.properties` – MySQL connection and Spring settings
- `demo/src/main/java/com/assessment/demo/controllers/` – REST endpoints
  - `CustomerController` (`/api/customers`) – CRUD + DELETE cascades via DB
  - `VacationController` (`/api/vacations`) – list vacations
  - `ExcursionController` (`/api/excursions`) – list excursions
  - `CheckoutController` (`/api/checkout/purchase`) – place order
- `demo/src/main/java/com/assessment/demo/entities/` – JPA entities mapped to tables
  - `Customer`, `Cart`, `CartItem`, `Vacation`, `Excursion`, `Division`, `Country`, `StatusType`
  - Relationships:
    - `Customer (1) -> (many) Cart` (FK: `carts.customer_id`)
    - `Cart (1) -> (many) CartItem` (FK: `cart_items.cart_id`)
    - `CartItem (many) <-> (many) Excursion` via join table `excursion_cartitem`
    - `Vacation (1) -> (many) Excursion` (FK: `excursions.vacation_id`)
- `demo/src/main/java/com/assessment/demo/dao/` – Spring Data repositories
  - `CustomerRepository`, `CartRepository`, `CartItemRepository`, etc.
- `demo/src/main/java/com/assessment/demo/services/` – Business logic (checkout flow)
  - `CheckoutService`, `CheckoutServiceImpl`, `Purchase`, `PurchaseResponse`

Key backend details to replicate
- DB: MySQL 8 on `localhost:3306` with schema `full-stack-ecommerce`
- Credentials: set in `application.properties` (`spring.datasource.*`)
- Hibernate DDL: `spring.jpa.hibernate.ddl-auto=none` (DB created via SQL scripts or manually)
- Base path: `spring.data.rest.base-path=/api` (all endpoints start with `/api`)
- Cascades: ensure DB FKs use `ON DELETE CASCADE` for:
  - `carts.customer_id` → customers.customer_id
  - `cart_items.cart_id` → carts.cart_id
  - `excursion_cartitem.cart_item_id` → cart_items.cart_item_id

## Frontend Structure
- `client/src/app/views/` – Routed pages (customers, vacations, excursions, cart)
  - `view-customer/` – list, delete, and navigate to detail/edit
  - `add-customer/`, `edit-customer/` – CRUD forms
  - `vacation/`, `excursion/`, `excursion-detail/` – browse/select
  - `cart/`, `cart-summary/`, `order-confirmation/`
- `client/src/app/services/` – Shared services
  - `purchase-data.service.ts` – in-memory cart and checkout data
- `client/src/app/model/` – Typed models and API response shapes
  - DTOs under `model/dto/`
- `client/src/environments/` – API base URL and environment flags
- Dev proxy: `client/proxy.conf.json` routes `/api` → `http://localhost:8080`

Key frontend details to replicate
- Angular CLI v14 project, `ng serve` uses proxy for `/api`
- API calls target relative paths like `/api/customers`
- Delete customer calls `DELETE /api/customers/{id}` and refreshes the list on success

## Key Features
- View vacations, excursion details, and pricing
- Add excursions to a cart, update party size, see package total
- Customer CRUD (add, view, edit, delete)
- Checkout flow posts a purchase to the backend
- Proxy setup for local dev: Angular `/api` → Spring Boot on port 8080
- Safe deletes: DB-level ON DELETE CASCADE from customers → carts → cart_items → excursion_cartitem

![alt text](image-3.png)

## APIs (examples)
- `GET /api/customers` – list customers
- `GET /api/customers/{id}` – get a customer
- `POST /api/customers` – create (DTO expected)
- `PUT /api/customers/{id}` – update
- `DELETE /api/customers/{id}` – delete (cascades through carts/items)
- `GET /api/vacations`, `GET /api/excursions` – browse data
- `POST /api/checkout/purchase` – place an order

## ⚡ Quick Start
```bash
# Backend
cd demo
mvn spring-boot:run

# Frontend
cd ../client
npm install
ng serve --proxy-config proxy.conf.json
```
Backend: http://localhost:8080
Frontend: http://localhost:4200
API calls to `/api/...` are proxied to the backend

## Common Commands
Frontend
```bash
cd client
npm install
ng serve --proxy-config proxy.conf.json
ng build
ng test
```
Backend
```bash
cd demo
mvn spring-boot:run
mvn test
```


## Database Setup Options
- Use existing local MySQL schema (recommended for dev). Ensure these FKs have cascades:
  - `carts.customer_id` → customers.customer_id ON DELETE CASCADE
  - `cart_items.cart_id` → carts.cart_id ON DELETE CASCADE
  - `excursion_cartitem.cart_item_id` → cart_items.cart_item_id ON DELETE CASCADE
- Or initialize from SQL scripts:
  - Copy a schema file into `demo/src/main/resources/database/schema.sql`
  - In `application.properties` set:
    ```properties
    spring.jpa.hibernate.ddl-auto=none
    spring.sql.init.mode=always
    spring.sql.init.schema-locations=classpath:database/schema.sql
    ```
  - Restart the backend; Spring Boot will run the script



# 💡 Why This Project?
- Built full-stack CRUD with real-world patterns
- Learned database cascade deletes, CORS, and deployment
- Designed for easy recruiter review and demo



## Developer Tips
- Angular environment: `client/src/environments/environment.ts`
- Proxy: `client/proxy.conf.json` (already wired in `angular.json` dev serve)
- Data models/DTOs: `client/src/app/model` and `client/src/app/model/dto`
- Purchase data service (cart state): `client/src/app/services/purchase-data.service.ts`
- Controllers (backend): `demo/src/main/java/com/assessment/demo/controllers`
- Entities (backend): `demo/src/main/java/com/assessment/demo/entities`



