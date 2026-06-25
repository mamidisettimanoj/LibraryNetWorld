# Beginner Setup Guide

## Required software

1. Java JDK 17 or newer
2. Spring Tools Suite
3. PostgreSQL and pgAdmin, or Docker Desktop
4. Node.js 20 or newer
5. Git

## Step 1: Database

### With pgAdmin

1. Open pgAdmin.
2. Connect to your PostgreSQL server.
3. Right-click **Databases → Create → Database**.
4. Enter `librarynet`.
5. Keep owner as `postgres`.
6. Confirm that the PostgreSQL password matches the backend configuration.

Default backend configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/librarynet
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### With Docker

From the project root:

```powershell
docker compose up -d
```

## Step 2: Backend in Spring Tools

1. Import `backend` as an existing Maven project.
2. Check that the project uses JDK 17 or later.
3. Wait until Maven finishes downloading dependencies.
4. Run `LibraryNetApplication` as a Spring Boot App.
5. Open `http://localhost:8080/api/health`.

Expected response:

```json
{"status":"UP","service":"LibraryNet Backend"}
```

## Step 3: Frontend

```powershell
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

## Step 4: First demonstration

1. Open Dashboard.
2. Add a book in Book Catalog.
3. Register a member.
4. Borrow an available book.
5. Return the loan.
6. Generate recommendations.
7. Open DSA Lab and run BST vs AVL, BFS, Dijkstra, sorting, and B+ Tree range query.

## Common errors

### PostgreSQL connection refused

Start PostgreSQL and confirm port `5432`.

### Password authentication failed

Change `DB_PASSWORD` or update `application.properties`.

### Port 8080 already used

Set environment variable `SERVER_PORT=8081` and update the frontend API URL.

### npm is not recognized

Install Node.js, close the terminal, and open it again.

### CORS error

Keep React on `http://localhost:5173`, or change `FRONTEND_ORIGIN` for the backend.
