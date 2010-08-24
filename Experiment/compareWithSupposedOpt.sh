#!/bin/bash

# Quick and dirty script for comparring darwin optimisation
# with supposed utility function optimisation

function rel2abs {
    echo "`cd \`dirname $1\`; pwd`/`basename $1`"
}

EXPECTED_ARGS=2
E_BADARGS=65
E_XCD=86

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` chrats-dir testplan-out-dir" >&2
  exit $E_BADARGS
fi

CHART_DIR=`rel2abs $1`
TESTPLAN_OUT_DIR=`rel2abs $2`

for DIR_SUPP in `find $TESTPLAN_OUT_DIR -iname '*_supp'`
do
    DIR_RULES=`echo $DIR_SUPP | sed s/_supp/_rules/`
    TEST_NAME=`basename $DIR_SUPP | sed s/_supp//`
    ./mergeEvReports.py `find $DIR_RULES -iname 'evolution_report.csv'` $DIR_RULES/merged_evolution_report.csv
    ./mergeEvReports.py `find $DIR_SUPP -iname 'evolution_report.csv'` $DIR_SUPP/merged_evolution_report.csv
    ./compare.py $CHART_DIR \
	$DIR_RULES/merged_evolution_report.csv ${TEST_NAME}_rules \
	$DIR_SUPP/merged_evolution_report.csv ${TEST_NAME}_supp \
	$DIR_SUPP/../${TEST_NAME}_comparison.pdf
    
done

exit 0