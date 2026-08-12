#!/usr/bin/env bash

cd liquibase
sh ./migrate.sh
if [ $? -ne 0 ]; then
  echo "ERROR: Liquibase migrations failed. Aborting build."
  exit 1
fi

cd ..
[ -n "$MANAGED_IDENTITY_USERNAME" ] && ./mi.sh "$MANAGED_IDENTITY_USERNAME"
