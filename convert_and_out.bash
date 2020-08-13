#!/bin/bash
set -e

# Create dex without Android SDK...
wget -nv https://maven.google.com/com/android/tools/r8/2.0.99/r8-2.0.99.jar
rm -rf out && mkdir out
cp target/lwjgl-glfw-*.jar out/lwjgl-glfw-classes.jar
java -cp r8-2.0.99.jar com.android.tools.r8.D8 --output out/lwjgl-glfw-converted.jar out/lwjgl-glfw-classes.jar

