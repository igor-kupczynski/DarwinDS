#!/bin/sh

cd Darwin
sbt clean package
cd ..
cp Darwin/target/scala_2.8.0/darwin_2.8.0-0.1.jar OneJar/main/darwin.jar

cd OneJar
jar -cvfm ../Distribution/darwin-full.jar boot-manifest.mf .
cd ..

zip Distribution.zip Distribution
