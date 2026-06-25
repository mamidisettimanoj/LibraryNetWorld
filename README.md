# LibraryNet Full-Stack

LibraryNet is an intelligent digital library and knowledge retrieval system built as a complete development project.

## Technology stack

- **Frontend:** React + Vite
- **Backend:** Spring Boot REST API
- **Database:** PostgreSQL
- **Development IDE:** Spring Tools / IntelliJ IDEA for backend, VS Code for frontend
- **DSA integration:** BST, AVL, B-Tree, B+ Tree, Segment Tree, Fenwick Tree, BFS, DFS, MST, shortest paths, sorting, greedy algorithms, and dynamic programming

## Main features

- Dashboard with catalog and borrowing analytics
- Book CRUD operations and keyword search
- Member registration and borrowing limits
- Borrow and return transactions with validation
- Journal and digital publication management
- Explainable recommendations
- Knowledge graph traversal and shortest paths
- DSA laboratory connected to real PostgreSQL data
- Sample-data reset endpoint
- Responsive React interface

## Project structure

```text
LibraryNet_FullStack/
├── backend/                 Spring Boot project
├── frontend/                React + Vite project
├── database/                PostgreSQL scripts
├── docs/                    Setup, API, and architecture guides
├── docker-compose.yml       PostgreSQL + pgAdmin
├── start-database.bat
├── start-backend.bat
├── start-frontend.bat
└── start-all.bat
```

## Quick start on Windows

### 1. Start PostgreSQL

Choose one method.

**Docker method**

```powershell
.\start-database.bat
```

This starts:

- PostgreSQL: `localhost:5432`
- Database: `librarynet`
- Username: `postgres`
- Password: `postgres`
- pgAdmin: `http://localhost:5050`

**Manual pgAdmin method**

Create a database named `librarynet`, then keep the default PostgreSQL credentials or update `backend/src/main/resources/application.properties`.

### 2. Run the Spring Boot backend

```powershell
.\start-backend.bat
```

Backend URL: `http://localhost:8080`

Health check: `http://localhost:8080/api/health`

### 3. Run the React frontend

Open another terminal:

```powershell
.\start-frontend.bat
```

Frontend URL: `http://localhost:5173`

## Open in Spring Tools

1. Open Spring Tools Suite.
2. Select **File → Import**.
3. Choose **Maven → Existing Maven Projects**.
4. Select the `backend` folder.
5. Wait for Maven dependencies to download.
6. Open `LibraryNetApplication.java`.
7. Right-click → **Run As → Spring Boot App**.

## Open React frontend

```powershell
cd frontend
npm install
npm run dev
```

## Build and test everything

```powershell
.\build-all.bat
```

Backend commands:

```powershell
cd backend
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Frontend commands:

```powershell
cd frontend
npm install
npm run lint
npm run build
npm run dev
```

## Database behavior

Spring Data JPA automatically creates and updates the tables. Sample books, members, publications, loans, and knowledge edges are inserted when the database is empty.

To restore sample data:

```http
POST /api/admin/reset-sample-data
```

## Important documentation

- `docs/BEGINNER_SETUP.md`
- `docs/ARCHITECTURE.md`
- `docs/API_ENDPOINTS.md`
- `docs/SPRING_TOOLS_GUIDE.md`
- `docs/VIVA_GUIDE.md`

## Default ports

| Service | Port |
|---|---:|
| React | 5173 |
| Spring Boot | 8080 |
| PostgreSQL | 5432 |
| pgAdmin | 5050 |

## Academic purpose

The frontend improves usability, PostgreSQL provides permanent storage, Spring Boot exposes REST APIs, and the original DSA implementations remain available through the DSA Lab page and algorithm endpoints.
