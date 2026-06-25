@echo off
docker compose up -d postgres pgadmin
echo PostgreSQL: localhost:5432
 echo pgAdmin: http://localhost:5050
pause
