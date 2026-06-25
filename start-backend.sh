#!/usr/bin/env bash
set -e
cd "$(dirname "$0")/backend"
./mvnw spring-boot:run
