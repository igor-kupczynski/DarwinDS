#!/bin/bash
# Quick and dirty script for generating charts after doing testplan

function rel2abs {
    echo "`cd \`dirname $1\`; pwd`/`basename $1`"
}

EXPECTED_ARGS=6
E_BADARGS=65
E_XCD=86

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` charts-dir report1 report1-name report2 report2-name out-filename" >&2
  exit $E_BADARGS
fi

CHARTS_DIR=`rel2abs $1`
R1_FILE=`rel2abs $2`
R1_NAME=$3
R2_FILE=`rel2abs $4`
R2_NAME=$5
OUT_FILE=`rel2abs $6`


R --slave --no-save --args "$R1_FILE,$R1_NAME,$R2_FILE,$R2_NAME,$OUT_FILE" \
	< $CHARTS_DIR/val_gen_compare.R

exit 0
