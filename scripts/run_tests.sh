#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_DIR="$ROOT_DIR/build/classes"

echo "[run-tests] limpando diretório de saída"
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

echo "[run-tests] compilando fontes"
javac -d "$BUILD_DIR" \
    $(find "$ROOT_DIR/src/main/java" -name '*.java' -print) \
    $(find "$ROOT_DIR/src/test/java" -name '*.java' -print)

echo "[run-tests] executando BPlusTreeInsertTest"
java -cp "$BUILD_DIR" BPlusTreeInsertTest

echo "[run-tests] executando BPlusTreeSearchTest"
java -cp "$BUILD_DIR" BPlusTreeSearchTest

echo "[run-tests] executando BPlusTreeSplitTest"
java -cp "$BUILD_DIR" BPlusTreeSplitTest

echo "[run-tests] executando BPlusTreeDeleteTest"
java -cp "$BUILD_DIR" BPlusTreeDeleteTest

echo "[run-tests] finalizado"
