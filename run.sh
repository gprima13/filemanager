#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

cd "$SCRIPT_DIR" || exit 1

mkdir -p logs

echo "Starting FileManager..."

java -jar filemanager-1.0.jar
--spring.config.additional-location=file:./config/
> logs/application.log 2>&1
