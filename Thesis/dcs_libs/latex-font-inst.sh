#!/bin/bash

#
#  LaTeX font installer
#  by Cezary Sobaniec <Cezary.Sobaniec@put.edu.pl>
#
#  $Id$
#

LOCAL_TEXMF=/usr/local/share/texmf

FONT=unknown
TYPE=public

case "$1" in
	luxi)
		FONT=ul9
		SOURCE_URL=ftp://www.ctan.org/tex-archive/fonts/LuxiMono
		TYPE=public
		;;
	bera)
		FONT=bera
		SOURCE_URL=ftp://www.ctan.org/tex-archive/fonts/bera
		TYPE=public
		;;
	gara*)
		FONT=ugm
		SOURCE_URL=ftp://www.ctan.org/tex-archive/fonts/urw/garamond
		TYPE=urw
		;;
	*)
		if [ -n "$1" ]
		then
			echo "Unknown font: $1"
		else
			echo "Usage:  font-inst.sh <luxi|bera|gara>"
		fi
		exit 1
esac

if [ ! -d "$LOCAL_TEXMF" ]
then
	mkdir -p $LOCAL_TEXMF || exit 1
fi
if [ ! -w "$LOCAL_TEXMF" ]
then
	echo "Cannot write to $LOCAL_TEXMF"
	exit 1
fi

TYPE1_SUBDIR=fonts/type1/$TYPE/$FONT
PFB_SUBDIR=fonts/type1/$TYPE/$FONT
AFM_SUBDIR=fonts/afm/$TYPE/$FONT
TMPDIR=$(mktemp -d /tmp/font-inst.XXXXXX || exit 1)


function do_exit() {
	rm -rf $TMPDIR
	exit $1
}

cd $TMPDIR || do_exit 1

echo "==>  Downloading the font..."
wget -nv -nH -nd -r $SOURCE_URL || do_exit 1

echo "==>  Installing..."
cd $LOCAL_TEXMF || do_exit 1
mkdir -p $PFB_SUBDIR || do_exit 1
mkdir -p $AFM_SUBDIR || do_exit 1
unzip -o $TMPDIR/$FONT.zip || do_exit 1
cp -v $TMPDIR/*.pfb $PFB_SUBDIR || do_exit 1
cp -v $TMPDIR/*.afm $AFM_SUBDIR || do_exit 1
MAP="$(find fonts/map -name "$FONT.map")"
if [ -z "$MAP" ]
then
	cp -v dvips/config/$FONT.map fonts/map/ || do_exit 1
fi
rm -f dvips/config/$FONT.map

echo "==>  Configuring..."
texconfig rehash
updmap --nomkmap --disable $FONT.map
updmap --enable Map=$FONT.map
do_exit
