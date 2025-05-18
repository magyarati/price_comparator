#!/usr/bin/env bash

# Get current date and time in YYYY-MM-DD_HH-MM-SS format
timestamp=$(date +"%F_%H-%M-%S")

# Create output filename with timestamp
output_file="price_comparator_${timestamp}.zip"

# Exit immediately if a command exits with a non-zero status
set -e

# Exclude patterns
excludes=(
  "*/node_modules/*"
  "*/build/*"
  "*/.gradle/*"
  "*/backend/build/*"
  "*/frontend/build/*"
  "*/.git/*"
  "*.DS_Store"
)

# Build the exclude arguments for zip
exclude_args=()
for pattern in "${excludes[@]}"; do
  exclude_args+=( -x "$pattern" )
done

# Create the ZIP archive
zip -r "$output_file" . "${exclude_args[@]}"

echo "Project packaged as $output_file"
