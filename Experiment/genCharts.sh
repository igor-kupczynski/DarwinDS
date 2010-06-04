#!/bin/bash
# Quick and dirty script for generating charts after doing testplan

function rel2abs {
    echo "`cd \`dirname $1\`; pwd`/`basename $1`"
}

EXPECTED_ARGS=5
E_BADARGS=65
E_XCD=86

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` --brief/--full criteria-no best-val charts-dir testplan-out-dir" >&2
  exit $E_BADARGS
fi

[[ "$1" == "--full" ]] && FULL=1 || FULL=0
[[ "$1" == "--brief" ]] && BRIEF=1 || BRIEF=0

if [[ ($FULL == 0) && ($BRIEF == 0) ]]
then
    echo "'$1' should be --full or --brief"
    exit $E_BADARGS
fi  

CRI_NO=$2
BEST=$3
CHARTS_DIR=`rel2abs $4`
TESTPLAN_OUT_DIR=`rel2abs $5`

for DIR in `find $TESTPLAN_OUT_DIR -name 'reports' -type d`
do
    echo "--- Charts for $DIR"
    R --slave --no-save --args "$CRI_NO,$BEST,$DIR/evolution_report.csv,$DIR/utilouter.pdf,$DIR/outer.csv" \
	< $CHARTS_DIR/util_outer.R
    if (( $FULL == 1 ))
    then
	R --slave --no-save --args "$CRI_NO,$DIR/evolution_report.csv,$DIR/utilgen.pdf" \
	    < $CHARTS_DIR/val_gen.R
	R --slave --no-save --args "$CRI_NO,$DIR/evolution_report.csv,$DIR/valweight.pdf" \
	    < $CHARTS_DIR/value_weight.R
	R --slave --no-save --args "$CRI_NO,$DIR/evolution_report.csv,$DIR/utilind.pdf" \
	    < $CHARTS_DIR/utility_ind.R
	R --slave --no-save --args "$CRI_NO,$DIR/dm_report.csv,$DIR/dm_choices.pdf" \
	    < $CHARTS_DIR/dm_selection.R
    fi
done
echo "--- Done"

exit 0
