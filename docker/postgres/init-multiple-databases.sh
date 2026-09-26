#!/bin/bash

set -e

for db in foodflow_users foodflow_restaurants foodflow_menu foodflow_orders foodflow_payments foodflow_notifications
do
    echo "Creating database: $db"

    psql \
      -v ON_ERROR_STOP=1 \
      --username "$POSTGRES_USER" \
      --dbname postgres \
      -c "CREATE DATABASE \"$db\";" || true
done
