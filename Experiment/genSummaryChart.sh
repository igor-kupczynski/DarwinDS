#!/bin/bash
# Quick and dirty script for generating summary and charts

function rel2abs {
    echo "`cd \`dirname $1\`; pwd`/`basename $1`"
}

EXPECTED_ARGS=3
E_BADARGS=65
E_XCD=86

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` best-val charts-dir testplan-out-dir" >&2
  exit $E_BADARGS
fi

BEST=$1
CHARTS_DIR=`rel2abs $2`
TESTPLAN_OUT_DIR=`rel2abs $3`

# We can set two criteria becasue brief option do not care
./genCharts.sh --brief 2 $BEST $CHARTS_DIR $TESTPLAN_OUT_DIR
./genSummary.py $TESTPLAN_OUT_DIR

DIR=$TESTPLAN_OUT_DIR
R --slave --no-save --args "2,$BEST,$DIR/summary.csv,$DIR/summary_short.csv,$DIR/summary1.pdf,$DIR/summary2.pdf" \
    < $CHARTS_DIR/summary.R

exit 0
