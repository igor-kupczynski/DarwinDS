#!/bin/sh

cd Darwin
sbt clean package
cd ..
ls Darwin/target/scala_2.8.0/darwin_2.8.0* > OneJar/darwin-version.txt
cp Darwin/target/scala_2.8.0/darwin_2.8.0*.jar OneJar/main/darwin.jar

cd OneJar
jar -cvfm ../Distribution/darwin-full.jar boot-manifest.mf .
cd ..

zip -r Distribution.zip Distribution
