#!/bin/bash
echo "============================================"
echo "  Hospital Management System - Builder"
echo "============================================"

# Create output directory
mkdir -p ../out/data

echo "Compiling all Java files..."

javac -d ../out models/*.java managers/*.java gui/*.java Main.java

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] Compilation failed. Check errors above."
    exit 1
fi

echo "Compilation successful!"
echo "Starting application..."
echo ""

cd ../out
java Main
