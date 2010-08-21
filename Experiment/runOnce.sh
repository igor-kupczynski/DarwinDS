#!/bin/bash
# Quick and dirty script for running darwin

function rel2abs {
    echo "`cd \`dirname $1\`; pwd`/`basename $1`"
}

EXPECTED_ARGS=5
E_BADARGS=65
E_XCD=86

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` darwin-dir config-file problme-file out-prefix out-sub-prefix" >&2
  exit $E_BADARGS
fi

DARWIN_DIR=`rel2abs $1`
CONFIG_FILE=`rel2abs $2`
PROBLEM_FILE=`rel2abs $3`
OUT=`rel2abs $4`
OUT_DIR="$OUT/$5"
ITEMS=(
    "project/boot/scala-2.8.0.RC1/lib/scala-library.jar"
    "lib_managed/scala_2.8.0.RC1/compile/args4j-2.0.9.jar"
    "lib/jrs_2010-04-30.jar"
    "lib/ForemkaCore_3.1.0.jar"
    "lib_managed/scala_2.8.0.RC1/compile/trove-2.1.1.jar"
    "lib_managed/scala_2.8.0.RC1/compile/ini4j-0.5.1.jar")

JAR="$DARWIN_DIR/target/scala_2.8.0.RC1/darwin_2.8.0.RC1-0.1.jar"
CP="$JAR"

for idx in $(seq 0 $((${#ITEMS[@]} - 1)))
do
    CP="$CP:$DARWIN_DIR/${ITEMS[$idx]}"
done

MAIN_CLASS="pl.poznan.put.darwin.model.Runner"

# Prepare output
if [ -d "$OUT_DIR" ]; then 
    echo "Directory $OUT_DIR exists. Do not want to overwrite!" >&2
    exit -1
fi

mkdir -p $OUT_DIR
mkdir "$OUT_DIR/reports"
mkdir "$OUT_DIR/rules"
mkdir "$OUT_DIR/input"

cd $OUT_DIR || {
    echo "Cannot change to necessary directory." >&2
    exit $E_XCD;
}
cp $CONFIG_FILE input/config.ini
cp $PROBLEM_FILE input/problem.mod
java -Xms768m -Xmx768m -classpath "$CP" $MAIN_CLASS --config $CONFIG_FILE --problem $PROBLEM_FILE
cd - > /dev/null

exit 0
