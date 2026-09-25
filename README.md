# Product Management System REST API

A Spring Boot REST API + Thymeleaf UI for managing product inventory: full CRUD (add, view, update via PUT/PATCH, delete one or all), duplicate checks on name and serial number, AJAX-powered search/pagination on the UI, Bean Validation, custom exceptions mapped to proper HTTP codes, and a Postman collection.

## Overview

This project is a small but complete product-inventory manager built with Spring Boot. It exposes a JSON REST API for programmatic use (Postman, other services) and a server-rendered Thymeleaf UI on top of the same backend, with the product list page fetching and updating data via AJAX rather than full-page reloads.

## Features

- **Add** a product — rejected if the product name (case-insensitive) or serial number (case-sensitive) already exists
- **List / view** products, individually or all at once
- **Full update (PUT)** — replaces every field
- **Partial update (PATCH)** — only overwrites fields actually supplied, using a dedicated request DTO so `quantity: 0` or `price: 0` can be sent intentionally (not confused with "not sent")
- **Delete** a single product by ID, or **delete all** in one shot
- **Search**, on both the REST API and the UI: pick a field (ID, Name, Serial Number, Brand, or Category — image excluded) and a value; an empty value returns everything
- **Pagination** with a selectable page size (5 / 10 / 25 / 50 / 100)
- **AJAX-driven list page**: search, pagination, and delete all update the table in place with no full-page reload; flash messages fade in, hold for 7 seconds, then fade out with the layout collapsing smoothly back
- Bean Validation on required fields (`@NotBlank`, `@NotNull`, `@DecimalMin`, etc.)
- Centralized exception handling: `404` for not found, `409` for duplicates, `400` for validation errors, each with a small JSON error body

## Tech Stack

- Java 21, Spring Boot 4.1.1
- Spring Data JPA + Hibernate, MySQL
- Spring Validation (Jakarta Bean Validation)
- Thymeleaf, Bootstrap 5, Font Awesome
- Vanilla JavaScript (`fetch`) for the AJAX list page — no frontend framework
- Maven

## Project Structure

```
com.example.ProductManagementSystem.product
├── entity/       Product, Brand, Category
├── repository/   ProductRepository  (CRUD + native SQL search/pagination query)
├── service/      ProductService     (business rules: uniqueness checks, PATCH semantics, search)
├── controller/   ProductController  (REST API under /api/products, MVC views under /products)
├── dto/          ProductPatchRequest (nullable-field DTO used for PATCH)
└── exception/    ProductNotFoundException, ProductAlreadyExistsException, ProductExceptionHandler

src/main/resources/templates/products/
├── list.html     Product table: AJAX search, pagination, page size, delete, delete all
├── form.html     Shared Add / Full-Update form (with live image preview)
└── view.html     Read-only single-product detail page
```

## Entity Fields

| Field         | Type         | Notes                                              |
|---------------|--------------|-----------------------------------------------------|
| `productID`   | `Integer`    | Auto-generated (`IDENTITY`), primary key            |
| `serialNumber`| `String`     | Required, unique (case-sensitive), max 100 chars    |
| `productName` | `String`     | Required, unique (case-insensitive), max 250 chars  |
| `brand`       | `Brand` enum | Required                                            |
| `category`    | `Category` enum | Required                                         |
| `quantity`    | `int`        | ≥ 0                                                  |
| `price`       | `BigDecimal` | Required, > 0                                       |
| `imageUrl`    | `String`     | Optional, max 500 chars                             |

## Getting Started

1. Clone the repo and open it in your IDE.
2. Add the MySQL driver to `pom.xml` if not already present:
   ```xml
   <dependency>
       <groupId>com.mysql</groupId>
       <artifactId>mysql-connector-j</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```
3. Configure `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/product_management
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```
4. Create the `product_management` database in MySQL, then run:
   ```
   mvn spring-boot:run
   ```
5. Visit `http://localhost:8080/products` for the UI, or hit the REST API at `http://localhost:8080/api/products`.

## REST API Endpoints

| Method | Endpoint                        | Description                                                        |
|--------|----------------------------------|----------------------------------------------------------------------|
| POST   | `/api/products`                  | Add a new product                                                   |
| GET    | `/api/products`                  | List all products                                                   |
| GET    | `/api/products/{id}`             | Get a product by ID                                                 |
| GET    | `/api/products/search`           | Search one field (`field=id\|name\|serialNumber\|brand\|category`, `value=`), paginated (`page=`, `size=`) |
| PUT    | `/api/products/{id}`             | Full update of a product                                            |
| PATCH  | `/api/products/{id}`             | Partial update of a product (nullable fields — only sent ones change) |
| DELETE | `/api/products/{id}`             | Delete a product by ID                                              |
| DELETE | `/api/products`                  | Delete all products                                                 |

### Search endpoint details

`GET /api/products/search?field=name&value=Dell&page=0&size=10`

- `field` — one of `id`, `name`, `serialNumber`, `brand`, `category` (default omitted/blank matches everything)
- `value` — the text to match (case-insensitive except for `id`); blank returns everything
- `page` — zero-based page index (default `0`)
- `size` — page size (default `10`)

Returns a Spring `Page<Product>` JSON object: `content`, `totalElements`, `totalPages`, `number`, `size`, `first`, `last`, `numberOfElements`.

### Error response shape

```json
{
  "timestamp": "2026-09-26T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "No product found with ID 42"
}
```

## Web UI Routes (Thymeleaf)

| Method | Route                     | Description                          |
|--------|----------------------------|----------------------------------------|
| GET    | `/products`                | Product list page (data loads via AJAX) |
| GET    | `/products/new`            | Add-product form                     |
| POST   | `/products`                | Submits the add-product form         |
| GET    | `/products/{id}/view`      | Read-only product detail page        |
| GET    | `/products/{id}/edit`      | Full-update (PUT-style) form          |
| POST   | `/products/{id}/edit`      | Submits the full-update form         |
| POST   | `/products/{id}/delete`    | Deletes a product (non-AJAX fallback route; the list page itself deletes via the REST API) |
| POST   | `/products/delete-all`     | Deletes all products (non-AJAX fallback route) |

## Testing with Postman

A ready-made collection is included: **`ProductManagementSystem.postman_collection.json`**

To use it:
1. Open Postman → **Import** → select the file.
2. It defines a `baseUrl` variable (`http://localhost:8080/api/products`) and a `productId` variable (`1`) — update `productId` after adding your own product.
3. Suggested run order: Add → Get All → Get By ID → Search → Update (PUT) → Patch → Delete By ID → Delete All.
