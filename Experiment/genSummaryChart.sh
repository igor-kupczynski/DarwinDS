#!/bin/bash
# Quick and dirty script for generating summary and charts

function rel2abs {
    echo "`cd \`dirname $1\`; pwd`/`basename $1`"
}

EXPECTED_ARGS=4
E_BADARGS=65
E_XCD=86

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` best-val charts-dir testplan-out-dir problem-name" >&2
  exit $E_BADARGS
fi

BEST=$1
CHARTS_DIR=`rel2abs $2`
TESTPLAN_OUT_DIR=`rel2abs $3`
P_NAME=$4

# We can set two criteria becasue brief option do not care
./genCharts.sh --brief 2 $BEST $CHARTS_DIR $TESTPLAN_OUT_DIR

echo "+--------------------------------------------------------------------+"
echo "|   GOT BRIEF CHARTS                                                 |"
echo "+--------------------------------------------------------------------+"

./genSummary.py $TESTPLAN_OUT_DIR

echo "+--------------------------------------------------------------------+"
echo "|   GOT SUMMARY                                                      |"
echo "+--------------------------------------------------------------------+"

DIR=$TESTPLAN_OUT_DIR
R --slave --no-save --args "2,$BEST,$DIR/summary.csv,$DIR/summary_short.csv,$DIR/summary1.pdf,$DIR/summary2.pdf" \
    < $CHARTS_DIR/summary.R

echo "+--------------------------------------------------------------------+"
echo "|   GOT SUMMARY CHART                                                |"
echo "+--------------------------------------------------------------------+"


for D in `find $DIR -iname 'base'`
do
    R --slave --no-save --args "2,$BEST,$P_NAME,$D/summary.csv,$D/summary.pdf" < ../Chart/util_outer_summary.R
done

exit 0
