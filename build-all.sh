#!/usr/bin/env bash
set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT/backend" && ./mvnw clean test package
cd "$ROOT/frontend" && npm install && npm run lint && npm run build
