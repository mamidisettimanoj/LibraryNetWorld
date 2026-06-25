# Spring Tools Guide

1. Keep the root project folder anywhere on your computer.
2. In Spring Tools, import only the `backend` folder as a Maven project.
3. Do not import `frontend` as a Java project.
4. Use `src/main/java` for Java classes.
5. Use `src/main/resources/application.properties` for configuration.
6. Run `LibraryNetApplication.java`.
7. Use the **Boot Dashboard** to stop and restart the backend.

## Important packages

- `controller`: REST endpoints
- `service`: business logic
- `repository`: database access
- `domain`: PostgreSQL entities
- `dto`: request and response objects
- `dsa`: original data structures and algorithms

## Changing database password

Set an environment variable in the Spring Boot run configuration:

```text
DB_PASSWORD=your_postgresql_password
```
