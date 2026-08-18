#!/usr/bin/env sh
set -eu

project_dir=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
output_dir="$project_dir/dist"
source_apk="$project_dir/app/build/outputs/apk/debug/app-debug.apk"
target_apk="$output_dir/gedu-debug.apk"

cd "$project_dir"
./gradlew assembleDebug

mkdir -p "$output_dir"
cp "$source_apk" "$target_apk"

echo "APK gerado com sucesso: $target_apk"

