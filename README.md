# CebuGuide

CebuGuide is a web-based tourist spot guide and itinerary planner for Cebu (IT342 individual project).

**Project Increment 1 - User Accounts and Authentication** implements:

1. **User account creation** - registration with validation, duplicate-email protection, BCrypt password hashing, and role assignment.
2. **User login and logout** - JWT-based login, error messages for invalid credentials, protected pages and endpoints, and logout that revokes the token on the server.
3. **Role-based dashboards** - separate Tourist and Administrator dashboards and menus, with access control enforced in both the React frontend and the Spring Boot backend.

## Tech stack

| Layer | Technology |
|---|---|
| Frontend | React (Vite), React Router, Axios |
| Backend | Java 21+, Spring Boot 4.1, Spring Security (OAuth2 Resource Server, JWT), Spring Data JPA |
| Database | MySQL 8 |

## Prerequisites

- Java 21 or newer (JDK)
- Node.js 20 or newer
- MySQL 8 running on `localhost:3306`
- Git

## 1. Database setup

Create an empty database (the tables are created automatically when the backend starts):

```sql
CREATE DATABASE cebuguide CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 2. Run the backend

The backend reads its settings from environment variables. Only `DB_PASSWORD` is needed if your MySQL user is `root`.

| Variable | Purpose | Default |
|---|---|---|
| `DB_USERNAME` | MySQL user | `root` |
| `DB_PASSWORD` | MySQL password | empty |
| `DB_HOST`, `DB_PORT`, `DB_NAME` | MySQL location | `localhost`, `3306`, `cebuguide` |
| `JWT_SECRET` | Secret used to sign login tokens (32+ characters) | development value in `application.properties` |
| `ADMIN_EMAIL` | Email of the seeded administrator | `admin@cebuguide.local` |
| `ADMIN_PASSWORD` | Password of the seeded administrator | `Admin12345` |

**Windows (PowerShell)**

```powershell
cd backend
$env:DB_PASSWORD = "your-mysql-password"
.\mvnw.cmd spring-boot:run
```

**macOS / Linux**

```bash
cd backend
DB_PASSWORD="your-mysql-password" ./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. On the first start an administrator account is created automatically.

## 3. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. The dev server forwards `/api` requests to the backend on port 8080.

## Demo accounts

| Role | Email | Password |
|---|---|---|
| Administrator (seeded) | `admin@cebuguide.local` | `Admin12345` (or your `ADMIN_PASSWORD`) |
| Tourist | create one on the **Register** page | your own |

## How to test the three features

1. **Account creation** - open `/register`. Try an empty form, a weak password, mismatched passwords, and an email that is already registered; each shows an error. A valid form creates a Tourist account.
2. **Login and logout** - log in with a wrong password to see "Invalid email or password." Log in correctly, reload the page (the session stays), then click **Log out**. The token is revoked on the server, so the old token no longer works.
3. **Role-based access** - log in as a tourist and open `/admin/dashboard`: you are sent to the "Access denied" page. Log in as the administrator to see the admin dashboard and the Users list. Without a token, any protected page redirects to the login page.

## REST API

| Method and path | Access | Description |
|---|---|---|
| `POST /api/auth/register` | public | Create a Tourist account |
| `POST /api/auth/login` | public | Log in and receive a token |
| `POST /api/auth/logout` | logged in | Revoke the current token |
| `GET /api/auth/me` | logged in | Current user |
| `GET /api/tourist/dashboard` | TOURIST | Tourist dashboard data |
| `GET /api/admin/dashboard` | ADMIN | Administrator dashboard data |
| `GET /api/admin/users` | ADMIN | List of all accounts |

Protected endpoints require the header `Authorization: Bearer <token>`. A missing or invalid token returns **401**; a valid token with the wrong role returns **403**.

## Security notes

- Passwords are stored only as **BCrypt** hashes and are never returned by the API.
- Login tokens are signed JWTs that expire after 60 minutes. Logging out stores the token's ID in the `revoked_tokens` table, and every request checks it.
- Roles are checked on the server for every protected endpoint. The frontend route guards only improve the user experience.
- The browser keeps the token in `sessionStorage`, so it is removed when the tab is closed.
- Secrets are read from environment variables. Set your own `JWT_SECRET` and `ADMIN_PASSWORD` outside development.

## Project structure

```
backend/   Spring Boot API (config, controller, service, repository, model, dto, exception)
frontend/  React app (auth context, route guards, pages, layout)
```
