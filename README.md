# Wallet Service

A simple **wallet system** built using **Spring Boot**, **PostgreSQL**, **Redis**, and **Docker**.

The service supports wallet operations such as **top-up, bonus credit, purchases, and balance queries** for multiple asset types.

---

# Tech Stack

* Java (Spring Boot)
* PostgreSQL
* Redis
* Docker & Docker Compose

---

# Running the Project

Make sure you have **Docker** and **Docker Compose** installed.

### Start all services

```bash
docker compose up --build
```

This will start:

* Spring Boot application
* PostgreSQL database
* Redis server

The application will run on:

```
http://localhost:8080
```

---

# API Usage

All **POST APIs require an Idempotency Key** in the request header.

### Header

```
Idempotency-Key: <random-string>
```

Example:

```
Idempotency-Key: 9a7c1d82-5e9f-4e8f-a8c5-123456789abc
```

---

# Request Body Format

```json
{
  "userId": 1,
  "assetId": 1,
  "amount": 200
}
```

### Asset Types

| Asset ID | Asset    |
| -------- | -------- |
| 1        | Gold     |
| 2        | Diamonds |
| 3        | Loyalty  |

---

# API Endpoints

### Top Up Wallet

```
POST /wallet/topup
```

Adds balance to a user's wallet.

---

### Bonus Credit

```
POST /wallet/bonus
```

Adds bonus assets to the user's wallet.

---

### Purchase

```
POST /wallet/purchase
```

Deducts assets from the user's wallet when making a purchase.

---

### Get Wallet Assets

```
GET /wallet/purchase/wallet/assets/{assetId}?userId={userId}
```

Example:

```
GET /wallet/purchase/wallet/assets/1?userId=1
```

Returns the balance of a specific asset for the user.

---

### Get System Treasury

```
GET /wallet/system/treasury
```

Returns the system treasury balances.

---

### Get System Revenue

```
GET /wallet/system/revenue
```

Returns the total system revenue.

---

# Notes

* All POST APIs are **idempotent** using the `Idempotency-Key` header.
* Redis is used for **request idempotency and transaction locking**.
* PostgreSQL stores wallet balances and transaction records.

---

# Example Request

### Topup Example

```
POST /wallet/topup
```

Header

```
Idempotency-Key: random-123
```

Body

```json
{
  "userId": 1,
  "assetId": 1,
  "amount": 200
}
```

---

# Stop Services

```bash
docker compose down
```
