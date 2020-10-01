#!/bin/bash
set -e

cd ..
git clone https://github.com/PojavLauncherTeam/lwjglx
cd lwjgl3-glfw-java

cp -R ../lwjglx/src/main/java/org src/main/java/

mvn -B package --file pom.xml

