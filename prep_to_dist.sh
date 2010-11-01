#!/bin/bash

rm -rf Distribution
mkdir Distribution
cd Distribution
mkdir etc
mkdir log
mkdir -p problems/no_uncertainty
mkdir reports
mkdir rules

cd problems
cp ../../Darwin/etc/problems/dtlz1_c4.mod ./
cp ../../Darwin/etc/problems/dtlz1_c10.mod ./
cp ../../Darwin/etc/problems/dtlz7_c4.mod ./
cp ../../Darwin/etc/problems/presentation.mod ./

cp ../../Darwin/etc/problems/knapsack300_darwin.mod no_uncertainty/knapsack_c2.mod
cp ../../Darwin/etc/problems/surface_darwin.mod no_uncertainty/surface_dtlz_c3.mod

cd ../..

rm -rf OneJar
mkdir OneJar
cd OneJar
wget http://heanet.dl.sourceforge.net/project/one-jar/one-jar/one-jar-0.97/one-jar-boot-0.97.jar
unzip one-jar-boot-0.97.jar
rm one-jar-boot-0.97.jar
echo "One-Jar-Main-Class: pl.poznan.put.darwin.RunnerGUI" >> boot-manifest.mf

mkdir main
mkdir lib
cp ../Darwin/lib_managed/scala_2.8.0/compile/*.jar lib/
cp ../Darwin/lib/*.jar lib/
cp ../Darwin/project/boot/scala-2.8.0/lib/scala-library.jar lib/

cd ..



