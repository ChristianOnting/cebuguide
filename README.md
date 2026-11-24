# CebuGuide

CebuGuide is a web-based tourist spot guide and itinerary planner for Cebu (IT342 individual project).

## Implemented increments

**Increment 1 - User Accounts and Authentication**: registration with validation, duplicate-email protection, BCrypt hashing, role assignment; JWT login and logout (the token is revoked on the server); role-based Tourist and Administrator dashboards enforced in both frontend and backend.

**Increment 2 - User Profile and Password Management**:

1. **User profile** - logged-in users view and edit their own name and email. The account is taken from the login token, so nobody can edit another user's profile.
2. **Change password** - requires the current password, a valid and confirmed new password that differs from the old one. The old password stops working and all earlier login tokens are rejected, so the user logs in again.
3. **Forgot and reset password** - the login page links to "Forgot password?". A reset link is sent by email. The link holds a random token that expires after 30 minutes and works only once. Only the SHA-256 hash of the token is stored. The page always answers the same way, so it does not reveal whether an email is registered.

## Tech stack

| Layer | Technology |
|---|---|
| Frontend | React (Vite), React Router, Axios |
| Backend | Java 21+, Spring Boot 4.1, Spring Security (OAuth2 Resource Server, JWT), Spring Data JPA, Spring Mail |
| Database | MySQL 8 |
| Email | Gmail SMTP (App Password) |

## Prerequisites

- Java 21 or newer (JDK), Node.js 20 or newer, MySQL 8 on `localhost:3306`, Git
- A Gmail account with 2-Step Verification and a Google **App Password** (to send reset emails)

## 1. Database setup

```sql
CREATE DATABASE cebuguide CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

The tables (`users`, `revoked_tokens`, `password_reset_tokens`) are created automatically when the backend starts.

## 2. Run the backend

Settings are read from environment variables.

| Variable | Purpose | Default |
|---|---|---|
| `DB_USERNAME`, `DB_PASSWORD` | MySQL login | `root`, empty |
| `DB_HOST`, `DB_PORT`, `DB_NAME` | MySQL location | `localhost`, `3306`, `cebuguide` |
| `JWT_SECRET` | Secret that signs login tokens (32+ characters) | development value |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | Seeded administrator | `admin@cebuguide.local`, `Admin12345` |
| `MAIL_USERNAME` | Gmail address that sends the reset email | none |
| `MAIL_PASSWORD` | Google App Password (16 characters) | none |
| `MAIL_FROM` | Optional sender address | the Gmail address |
| `FRONTEND_URL` | Address used in the reset link | `http://localhost:5173` |

**Windows (PowerShell)**

```powershell
cd backend
$env:DB_PASSWORD = "your-mysql-password"
$env:MAIL_USERNAME = "your.address@gmail.com"
$env:MAIL_PASSWORD = "your-16-character-app-password"
.\mvnw.cmd spring-boot:run
```

**macOS / Linux**

```bash
cd backend
DB_PASSWORD="..." MAIL_USERNAME="..." MAIL_PASSWORD="..." ./mvnw spring-boot:run
```

If `MAIL_USERNAME` is not set, the backend writes the reset link to its console instead of emailing it (development only).

**Creating a Google App Password:** Google Account, then Security, turn on 2-Step Verification, then App passwords, create one named "CebuGuide", and copy the 16 characters.

## 3. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

## Demo accounts

| Role | Email | Password |
|---|---|---|
| Administrator (seeded) | `admin@cebuguide.local` | `Admin12345` (or your `ADMIN_PASSWORD`) |
| Tourist | create one on **Register** (use an email you can read, to test password reset) | your own |

## How to test Increment 2

1. **Profile** - log in, open **My Profile**, change the name and save. The name in the top bar updates. Try an empty name, an invalid email, and an email that belongs to another account.
2. **Change password** - open **Change Password**. A wrong current password, a weak password, mismatched passwords, and the same password are rejected. After a valid change you are sent to the login page; the old password no longer works and the new one does.
3. **Forgot password** - on the login page click **Forgot password?**, enter your email, and open the link in the email. Set a new password and log in. Open the same link again: it is rejected because it was already used. A link older than 30 minutes is also rejected.

## REST API

| Method and path | Access | Description |
|---|---|---|
| `POST /api/auth/register` | public | Create a Tourist account |
| `POST /api/auth/login` | public | Log in and receive a token |
| `POST /api/auth/logout` | logged in | Revoke the current token |
| `GET /api/auth/me` | logged in | Current user |
| `GET /api/profile` | logged in | Own profile |
| `PUT /api/profile` | logged in | Edit own name and email |
| `POST /api/profile/change-password` | logged in | Change own password |
| `POST /api/auth/forgot-password` | public | Email a reset link |
| `GET /api/auth/reset-password/validate?token=` | public | Check that a reset link is still usable |
| `POST /api/auth/reset-password` | public | Set a new password with a reset token |
| `GET /api/tourist/dashboard` | TOURIST | Tourist dashboard data |
| `GET /api/admin/dashboard`, `GET /api/admin/users` | ADMIN | Administrator data |

Protected endpoints require `Authorization: Bearer <token>`. Missing or invalid tokens return **401**; the wrong role returns **403**; validation problems return **400** with the failing fields.

## Security notes

- Passwords are stored only as **BCrypt** hashes and are never returned by the API.
- Login tokens are signed JWTs (60 minutes). Logout, a password change, or a password reset invalidates earlier tokens.
- Reset tokens are 256-bit random values. Only a SHA-256 hash is stored; each token expires in 30 minutes and is deleted when used. Requesting a new link cancels older ones.
- The forgot-password endpoint gives the same answer for every email, so accounts cannot be discovered.
- Secrets (database, JWT, email) come from environment variables and are not committed.

## Project structure

```
backend/   Spring Boot API (config, controller, service, repository, model, dto, exception)
frontend/  React app (auth context, route guards, pages, layout)
docs/      Increment completion reports
```
