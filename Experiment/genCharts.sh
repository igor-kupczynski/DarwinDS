#!/bin/bash
# Quick and dirty script for generating charts after doing testplan

function rel2abs {
    echo "`cd \`dirname $1\`; pwd`/`basename $1`"
}

EXPECTED_ARGS=2
E_BADARGS=65
E_XCD=86

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` charts-dir testplan-out-dir" >&2
  exit $E_BADARGS
fi

CHARTS_DIR=`rel2abs $1`
TESTPLAN_OUT_DIR=`rel2abs $2`

for DIR in `find . -name 'reports' -type d`
do
    echo "--- Charts for $DIR"
    R --slave --no-save --args "$DIR/evolution_report.csv,$DIR/utilgen.pdf" < $CHARTS_DIR/val_gen.R
done
echo "--- Done"

exit 0
