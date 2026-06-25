# REST API Endpoints

Base URL: `http://localhost:8080/api`

## Health and analytics

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/health` | Backend health |
| GET | `/analytics/dashboard` | Dashboard statistics |
| GET | `/analytics/monthly-loans?months=12` | Borrowing trend |
| GET | `/analytics/categories` | Category distribution |

## Books

| Method | Endpoint |
|---|---|
| GET | `/books` |
| GET | `/books?q=AI` |
| GET | `/books/{id}` |
| POST | `/books` |
| PUT | `/books/{id}` |
| DELETE | `/books/{id}` |

Book request example:

```json
{
  "catalogId": 20,
  "title": "Advanced Java",
  "author": "Manoj",
  "category": "Programming",
  "publicationYear": 2026,
  "isbn": "9780000000020"
}
```

## Members and loans

| Method | Endpoint |
|---|---|
| GET | `/members` |
| POST | `/members` |
| PUT | `/members/{id}` |
| DELETE | `/members/{id}` |
| GET | `/loans` |
| POST | `/loans/borrow` |
| PUT | `/loans/{loanId}/return` |

Borrow request:

```json
{"memberId": 1, "bookId": 3}
```

## Publications and discovery

| Method | Endpoint |
|---|---|
| GET | `/publications` |
| GET | `/publications?startYear=2020&endYear=2025` |
| POST | `/publications` |
| DELETE | `/publications/{id}` |
| GET | `/discovery/recommendations/{catalogId}?limit=5` |

## DSA endpoints

| Endpoint | Algorithm |
|---|---|
| `/algorithms/tree-comparison/5` | BST vs AVL |
| `/algorithms/bfs/0` | BFS |
| `/algorithms/shortest-path?source=0&target=11` | Dijkstra |
| `/algorithms/mst` | Kruskal MST |
| `/algorithms/sort?algorithm=merge` | Sorting |
| `/algorithms/publication-range?startYear=2019&endYear=2025` | B+ Tree range |
| `/algorithms/optimization-demo` | Greedy and DP |
