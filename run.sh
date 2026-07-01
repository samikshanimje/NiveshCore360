#!/bin/bash
# NiveshCore360 Local Runner Script

# Resolve directory of this script
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$DIR"

echo "=========================================================="
echo "      NiveshCore360 Investment Management System          "
echo "=========================================================="
echo "Initializing Spring context and launching GUI..."
echo "----------------------------------------------------------"

# Launch application via Maven spring-boot plugin
mvn spring-boot:run
