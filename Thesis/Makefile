######################################################################
#
#  Generic Makefile for LaTeX/LyX documents
#  by Cezary Sobaniec <cezary.sobaniec@cs.put.poznan.pl>
#
#  $Id: Makefile 68 2009-10-31 18:16:57Z puszczyk $
#
######################################################################


# --------------------------------------------------------------------
# Getting started
# ===============
#
# For ease of use define the following function for bash interpreter:
# 
#   function mk() {
#     if [ -f Makefile ]
#     then
#       make "$@"
#     else
#       make -f $HOME/.../latex.mk "$@"
#     fi
#   }
#
# Thanks to this function you may run 'mk' command without Makefile.
# To get help run 'mk help'.
# --------------------------------------------------------------------

ifneq ($(wildcard Makefile),Makefile)
  MAKE=make -f $(HOME)/share/mk/Makefile.latex
endif
MAINFILE?=${firstword ${wildcard *.lyx} ${wildcard *.tex}}
ifeq (${MAINFILE},)
$(error Value of MAINFILE unknown.  Missing .lyx or .tex file.)
endif
MF:=$(MAINFILE:.lyx=)
MF:=$(MF:.tex=)

BACKUPFILE=$(shell basename ${PWD}).tar.gz

MONO?=no

PS_VIEWER?=gv -antialias -scale 2 -swap -resize
PDF_VIEWER?=acroread
PDF_VIEWER_OPTS?=
TEX_EDITOR?=gvim -geometry 90x52+370+0
DVI_VIEWER?=kdvi
DVI_VIEWER_OPTS?=
FIG2DEV_OPTS?=
SCALE?=1.1
HTMLOUTDIR?=HTML
PSTOPSOPS?=-pa4
FIGMAG?=1.5
CLEAN_FIGS?=yes
TMPFILE=latex.mk.tmp~

ifeq (${MONO},yes)
FIG2DEV_OPTS+=-N
endif

LYX_DOC_FILES=${wildcard *.lyx}
TEX_DOC_FILES=${wildcard *.tex}
TXT_DOC_FILES=${wildcard *.txt}
HTML_DOC_FILES=${TXT_DOC_FILES:.txt=.html}
XFIG_PICTURES?=${wildcard *.fig */*.fig}
SVG_PICTURES?=${wildcard *.svg */*.svg}
DIA_PICTURES?=${wildcard *.dia */*.dia}
PNG_PICTURES?=${wildcard *.png */*.png}
GIF_PICTURES?=${wildcard *.gif */*.gif}
JPG_PICTURES?=${wildcard *.jpg */*.jpg}
EPS_PICTURES=${wildcard *.eps */*.eps}
SOURCE_PICTURES=${XFIG_PICTURES} ${SVG_PICTURES} ${DIA_PICTURES} \
	${PNG_PICTURES} ${GIF_PICTURES} ${JPG_PICTURES}
PS_PICTURES=${XFIG_PICTURES:.fig=.eps} ${SVG_PICTURES:.svg=.eps} \
	${DIA_PICTURES:.dia=.eps} ${PNG_PICTURES:.png=.eps} \
	${GIF_PICTURES:.gif=.eps} ${JPG_PICTURES:.jpg=.eps}
EPS_PICTURES:=${filter-out ${PS_PICTURES},${EPS_PICTURES}}
PDF_PICTURES=${XFIG_PICTURES:.fig=.pdf} ${SVG_PICTURES:.svg=.pdf} \
	${PNG_PICTURES} ${GIF_PICTURES} ${JPG_PICTURES} \
	${EPS_PICTURES:.eps=.pdf} ${DIA_PICTURES:.dia=.pdf}
WEB_PICTURES=${XFIG_PICTURES:.fig=.png} ${SVG_PICTURES:.svg=.png} \
	${PNG_PICTURES} ${GIF_PICTURES:.gif=.png} ${JPG_PICTURES}


SKIP="****************************************************************"

export M4PATH=/usr/share/m4

BIBLIOGRAPHY=${MF}.bbl

ifdef INDEX
INDEXDEP=${MF}.ind
endif

#
# Pattern rules
#

%.pdf: %.fig
	@echo ${SKIP}
	@echo "*  Converting $< to pdf ..."
	@echo ${SKIP}
	fig2dev -L pdf ${FIG2DEV_OPTS} -p x $< > $@

%.eps: %.fig
	@echo ${SKIP}
	@echo "*  Converting $< to eps ..."
	@echo ${SKIP}
	fig2dev -L eps ${FIG2DEV_OPTS} $< > $@
#	fig2dev -L eps $< | ogonkify -ATH -F > $@

%.eps: %.svg
	@echo ${SKIP} 
	@echo "*  Converting $< to ps ..."
	@echo ${SKIP} 
	@inkscape -E $@ $<

%.pdf: %.svg
	@echo ${SKIP} 
	@echo "*  Converting $< to pdf ..."
	@echo ${SKIP} 
	@inkscape -A $@ $<

%.eps: %.gif
	@echo ${SKIP}
	@echo "*  Please convert GIF pictures to PNG format"
	@echo ${SKIP}
	exit 1

%.eps: %.png
	@echo ${SKIP}
	@echo "*  Converting $< to $@ ..."
	@echo ${SKIP}
	@if [ "${MONO}" = "yes" ]; then \
	   pngtopnm $< | ppmtopgm | pnmtops -noturn > $@; \
	 else \
	   pngtopnm $< | pnmtops -noturn > $@; \
	 fi

%.pdf: %.eps
	epstopdf $<

%.png: %.fig
	@echo ${SKIP}
	@echo "*  Running fig2dev for $< ..."
	@echo ${SKIP}
	fig2dev -L png ${FIG2DEV_OPTS} -m ${FIGMAG} $< $@

%.gif: %.fig
	@echo ${SKIP}
	@echo "*  Running fig2dev for $< ..."
	@echo ${SKIP}
	fig2dev -L gif ${FIG2DEV_OPTS} -m ${FIGMAG} $< $@

%.jpg: %.fig
	@echo ${SKIP}
	@echo "*  Running fig2dev for $< ..."
	@echo ${SKIP}
	fig2dev -L jpeg ${FIG2DEV_OPTS} -m ${FIGMAG} $< $@

%.dvi: %.tex
	@echo ${SKIP}
	@echo "*  Running LaTeX for $< ... "
	@echo ${SKIP}
	-test -f $*.aux && cp $*.aux $*.aux~
	@latex $<

%.pdf: %.tex
	@echo ${SKIP}
	@echo "*  Running PDFLaTeX for $< ..."
	@echo ${SKIP}
	-test -f $*.aux && cp $*.aux $*.aux~
	@pdflatex $<

%.ps: %.dvi
	@echo ${SKIP}
	@echo "*  Running dvips for $< ..."
	@echo ${SKIP}
	@if grep -q '\\geometry{.*landscape' $*.tex; then \
	   dvips -t landscape -o $@ $<; \
	 else \
	   dvips ${DVIPSOPS} $< -o $@; \
	 fi

%.tex: %.lyx
	@echo ${SKIP} 
	@echo "*  Exporting file $< to LaTeX ..."
	@echo ${SKIP} 
	@lyx -e latex $<

%.eps: %.dia
	@echo ${SKIP}
	@echo "*  Converting $< to eps ..."
	@echo ${SKIP}
	@dia -t eps $<

%.pdf: %.dia
	@${MAKE} -s $*.eps
	@echo ${SKIP}
	@echo "*  Converting $< to pdf ..."
	@echo ${SKIP}
	@epstopdf $*.eps > $@

# for reStructuredText
%.html: %.txt
	@echo ${SKIP} 
	@echo "*  Converting $< to HTML ..."
	@echo ${SKIP} 
	@rst2html $*.txt > $@


#
#  Targets
#

all: dvi

bibtex:
	@bibtex ${MF}

build:
	@rm -f ${MF}.dvi
	@${MAKE} -s dvi

buildpdf: finalpdf pclean

signal:
	@pkill -USR1 -U ${USER} ${DVI_VIEWER} || true

dvi: ${MF}.dvi

view: all viewdvi

viewdvi: ${MF}.dvi
	@${DVI_VIEWER} ${DVI_VIEWER_OPTS} ${MF}.dvi &

viewps: ${MF}.ps
	@echo ${SKIP}
	@echo "*  Running PS viewer ..."
	@echo ${SKIP}
	@${PS_VIEWER} ${MF}.ps &

viewfinal: final view

viewfinalps: final viewps

viewfinalpdf: final viewpdf

clean: do-clean

do-clean:
	@rm -f core *.dvi *.aux *.log *.[Bb][Aa][Kk] *.bbl *.blg *.toc *.lof \
		*.bck *.idx *.ind *.ilg *.swp SAVE.fig *~ ~* *.rtf \
		*.nav *.snm \
		*.ps *.pdf \#*\# *.out *.tmp \
		WARNINGS label.pl ${MAINFILE}.ps.gz \
		${BACKUPFILE} ${PS_PICTURES} \
		${XFIG_PICTURES:.fig=.pdf} ${XFIG_PICTURES:.fig=.png} \
		${DIA_PICTURES:.dia=.pdf} \
		${LYX_DOC_FILES:.lyx=.tex} ${LYX_DOC_FILES:.lyx=.txt} \
		${HTML_DOC_FILES} ui
	@rm -rf ${HTMLOUTDIR} ${MF}

clean-figs:
	@rm -f ${PS_PICTURES} ${XFIG_PICTURES:.fig=.pdf} ${XFIG_PICTURES:.fig=.png}

pclean:
	@test -f ${MF}.pdf && mv ${MF}.pdf ${MF}.pdf.SAVE
	@${MAKE} -s clean
	@test -f ${MF}.pdf.SAVE && mv ${MF}.pdf.SAVE ${MF}.pdf

figs: psfigs

psfigs:	${PS_PICTURES}

pdffigs: ${PDF_PICTURES}

webfigs: ${WEB_PICTURES}

backup: 
	@echo ${SKIP}
	@echo "*  Creating backup ... "
	@echo ${SKIP}
	@rm -f ${BACKUPFILE}
	@-tar cvfz ${BACKUPFILE} \
		*.tex *.lyx *.fig *.dia *.inc *.gif *.png *.jpg *.txt \
    *.bib Makefile *.sty *.cls ${BACKUPINC} *.eps 2> /dev/null

cp: backup
	mcopy -o ${BACKUPFILE} a:

bib: ${MF}.bbl

${MF}.bbl: ${BIBLIO}
	@if [ ! -r ${MF}.aux ]; then \
		echo ${SKIP}; \
	  echo "*  Running latex before BibTeX for ${MF} ..."; \
		echo ${SKIP}; \
		$(MAKE) --no-print-directory psfigs; \
		latex ${MF}.tex; \
		rm ${MF}.dvi; fi
	@echo ${SKIP}
	@echo "*  Running BibTeX for ${MF} ..."
	@echo ${SKIP}
	@bibtex ${MF}


${MF}.ind: ${MF}.tex
	@if [ ! -r ${MF}.idx ]; then \
		echo ${SKIP}; \
	  echo "*  Running latex before Makeindex for ${MF} ..."; \
		echo ${SKIP}; \
		latex ${MF}.tex; \
		rm ${MF}.dvi; fi
	@echo ${SKIP} 
	@echo "*  Running Makeindex for ${MF} ..."
	@echo ${SKIP} 
	@makeindex ${MF}

${MF}.dvi: ${MF}.tex ${INCLUDES} ${PS_PICTURES} ${BIBLIOGRAPHY} ${INDEXDEP}

ps: ${MF}.ps

${MF}.ps: ${MF}.dvi

pdf: ${MF}.pdf

${MF}.pdf: ${MF}.tex ${INCLUDES} ${PDF_PICTURES} ${BIBLIOGRAPHY} ${INDEXDEP}

viewpdf: ${MF}.pdf
	${PDF_VIEWER} ${PDF_VIEWER_OPTS} ${MF}.pdf

final:
	@rm -f ${MF}.dvi
	@${MAKE} all
	@if [ -f "${MF}.idx" ]; then \
	  ${MAKE} ${MF}.ind; \
	fi
	@if grep -q '\\bibdata{' ${MF}.aux; then \
	  ${MAKE} bib; \
	  rm -f ${MF}.dvi; \
		${MAKE} all; \
	fi
	@if ! cmp -s ${MF}.aux ${MF}.aux~; then \
	  rm -f ${MF}.dvi; \
	  ${MAKE} all; \
		if ! cmp -s ${MF}.aux ${MF}.aux~; then \
			rm -f ${MF}.dvi; \
			${MAKE} all; \
		fi; \
	fi

finalps: final ps

finalpdf:
	@make bibtex
	@rm -f ${MF}.pdf
	@${MAKE} pdf
	@if [ -f "${MF}.idx" ]; then \
	  ${MAKE} ${MF}.ind; \
	fi
	@if grep -q '\\bibdata{' ${MF}.aux; then \
	  ${MAKE} bib; \
	  rm -f ${MF}.pdf; \
		${MAKE} pdf; \
	fi
	@if ! cmp -s ${MF}.aux ${MF}.aux~; then \
	  rm -f ${MF}.pdf; \
	  ${MAKE} pdf; \
		if ! cmp -s ${MF}.aux ${MF}.aux~; then \
			rm -f ${MF}.pdf; \
			${MAKE} pdf; \
		fi; \
	fi

4perpage:
	@echo ${SKIP} 
	@echo "*  Dividing into 4 pages per A4 page ..."
	@echo ${SKIP} 
	@if grep -q '\\geometry{.*landscape' ${MF}.tex; then \
	   pstops ${PSTOPSOPS} \
	     '4:2@.5(0,14.85cm)+0@.5(10.5cm,14.85cm)+3@.5(0,0)+1@.5(10.5cm,0)' \
		   ${MF}.ps ${TMPFILE}; \
	 else \
	   pstops ${PSTOPSOPS} \
	     '4:0@.5(0,14.85cm)+1@.5(10.5cm,14.85cm)+2@.5(0,0)+3@.5(10.5cm,0)' \
		   ${MF}.ps ${TMPFILE}; \
	 fi
	mv ${TMPFILE} ${MF}.ps

4perpagelandscape:
	@echo ${SKIP} 
	@echo "*  Dividing into 4 pages per A4 page ..."
	@echo ${SKIP} 
	pstops ${PSTOPSOPS} \
	'4:0U@.5(10.5cm,14.85cm)+1U@.5(10.5cm,29.7cm)+2U@.5(21cm,14.85cm)+3U@.5(21cm,29.7cm)' \
		${MF}.ps ${TMPFILE}
	mv ${TMPFILE} ${MF}.ps

book: 
	@echo ${SKIP} 
	@echo "*  Dividing into 2 pages per A4 page in a book style ..."
	@echo ${SKIP} 
	@mv ${MF}.ps ${TMPFILE}
	pstops ${PSTOPSOPS} \
	 '4:1L@.7(21cm,0cm)+2L@.7(21cm,14.85cm),3L@.7(21cm,0cm)+0L@.7(21cm,14.85cm)' \
		${TMPFILE} ${MF}.ps
	@rm -f ${TMPFILE}

booksimple: 
	@echo ${SKIP} 
	@echo "*  Dividing into 2 pages per A4 page in a simple book style ..."
	@echo ${SKIP} 
	mv ${MF}.ps ${TMPFILE}
	psbook ${TMPFILE} | psnup -PA4 -2 > ${MF}.ps
	rm -f ${TMPFILE}

2perpage:
	@echo ${SKIP} 
	@echo "*  Dividing into 2 pages per A4 page ..."
	@echo ${SKIP} 
	pdfnup --nup 2x1 --paper a4paper ${MF}.pdf

2perpageps:
	@echo ${SKIP} 
	@echo "*  Dividing into 2 pages per A4 page ..."
	@echo ${SKIP} 
	mv ${MF}.ps ${TMPFILE}
	pstops ${PSTOPSOPS} '2:0L@.7(21cm,0cm)+1L@.7(21cm,14.85cm)' \
		${TMPFILE} ${MF}.ps
	rm -f ${TMPFILE}

2perpagethesame:
	@echo ${SKIP} 
	@echo "*  Dividing into 2 the same pages per A4 page ..."
	@echo ${SKIP} 
	mv ${MF}.ps ${TMPFILE}
	pstops ${PSTOPSOPS} '1:0L@.7(21cm,0cm)+0L@.7(21cm,14.85cm)' \
		${TMPFILE} ${MF}.ps
	rm -f ${TMPFILE}

2perpagerotate: 
	@echo ${SKIP} 
	@echo "*  Combining into 2 pages per A4 page ..."
	@echo ${SKIP} 
	@mv ${MF}.ps ${TMPFILE}
	pstops ${PSTOPSOPS} '4:0+2U(210mm,297mm),1+3U(210mm,297mm)' \
		${TMPFILE} ${MF}.ps
	@rm -f ${TMPFILE}

a5book: 
	@echo ${SKIP} 
	@echo "*  Combining into 2 pages per A4 page ..."
	@echo ${SKIP} 
	@mv ${MF}.ps ${TMPFILE}
	@psbook ${TMPFILE} ${MF}.ps
	@mv ${MF}.ps ${TMPFILE}
	pstops ${PSTOPSOPS} '2:0L(297mm,0)+1L(297mm,148.5mm)' \
		${TMPFILE} ${MF}.ps
	@rm -f ${TMPFILE}

2perpageoverlapthesame: 
	@echo ${SKIP} 
	@echo "*  Combining into 2 pages per A4 page ..."
	@echo ${SKIP} 
	@mv ${MF}.ps ${TMPFILE}
	@pstops ${PSTOPSOPS} '1:0+0(0,-14.85cm)' \
		${TMPFILE} ${MF}.ps
	@rm -f ${TMPFILE}

2perpageoverlap: 
	@echo ${SKIP} 
	@echo "*  Combining into 2 pages per A4 page ..."
	@echo ${SKIP} 
	@mv ${MF}.ps ${TMPFILE}
	@pstops ${PSTOPSOPS} '2:0+1(0,-14.85cm)' \
		${TMPFILE} ${MF}.ps

4perpageoverlapthesame: 
	@echo ${SKIP} 
	@echo "*  Combining into 2 pages per A4 page ..."
	@echo ${SKIP} 
	@mv ${MF}.ps ${TMPFILE}
	@pstops ${PSTOPSOPS} '1:0L@.7(21cm,14.85cm)+0L@.7(31.5cm,14.85cm)+0L@.7(21cm,0cm)+0L@.7(31.5cm,0cm)' ${TMPFILE} ${MF}.ps
	@rm -f ${TMPFILE}

refcard: finalps
	@echo ${SKIP} 
	@echo "*  Compiling a refcard"
	@echo ${SKIP} 
	pstops -pa3 '3:0+1(0cm,-140mm)+2(0cm,-280mm)' \
		${MF}.ps ${TMPFILE}
	psresize -Pa3 -pa4 ${TMPFILE} ${MF}.ps
	@rm -f ${TMPFILE}

scale:
	@pdfnup --nup 1x1 --scale ${SCALE} ${MF}.pdf
	@mv ${MF}-1x1.pdf ${MF}.pdf

scale2:
	@pdfnup --nup 1x1 --scale 1.2 --offset "-0.5cm -1cm" ${MF}.pdf
	@mv ${MF}-1x1.pdf ${MF}.pdf

edit:
	@if [ ! -f "${MF}.lyx" -a -f "${MF}.tex" ]; then \
	   ${TEX_EDITOR} ${MF}.tex; \
	 else \
	   lyx -geometry 813x713-0+0 ${MF}.lyx 2> /dev/null & \
	 fi;

update:
	@svn update

wc: export
	@detex ${MF}.tex | iconv -f latin2 -t utf8 | wc -w

export: ${MF}.tex

exportps:
	@echo ${SKIP} 
	@echo "*  Exporting file ${MF} to PostScript ..."
	@echo ${SKIP} 
	lyx -e ps ${MF}.lyx

exportpdf:
	@echo ${SKIP} 
	@echo "*  Exporting file ${MF} to PDF ..."
	@echo ${SKIP} 
	lyx -e pdf2 ${MF}.lyx

exporttxt:
	@echo ${SKIP} 
	@echo "*  Exporting file ${MF} to TXT ..."
	@echo ${SKIP} 
	lyx -e text ${MF}.lyx

install:
	@for URL in ${DESTURL}; \
	do \
	  scp ${MF}.pdf $${URL}; \
	done

handout: clean handoutconvert finalpdf
	@echo ${SKIP} 
	@echo "*  Dividing into 4 pages per A4 page ..."
	@echo ${SKIP} 
	pdfnup --nup 2x2 --paper a4paper --frame true --scale 0.96 \
	       --delta "2mm 2mm" ${MF}.pdf


handoutinstall:
	scp ${MF}-2x2.pdf ${DESTURL}

handoutconvert: export
	@echo ${SKIP}
	@echo "*  Converting to handout ..."
	@echo ${SKIP}
	@sed 's/^\\documentclass\[\(.*\)\]{beamer}$$/\\documentclass[handout,\1]{beamer}/' ${MF}.tex > ${MF}.tmp
	@mv ${MF}.tmp ${MF}.tex

color-oneside: clean export
	@sed 's/\\documentclass\[\(.*\)\]{\(.*\)}/\\documentclass[\1,web]{\2}/' \
	      ${MF}.tex > ${MF}-oneside.tex
	@mv ${MF}-oneside.tex ${MF}.tex
	@${MAKE} -s finalpdf

color-twoside: clean export
	@sed 's/\\documentclass\[\(.*\)\]{\(.*\)}/\\documentclass[\1,webtwoside]{\2}/' \
	      ${MF}.tex > ${MF}-oneside.tex
	@mv ${MF}-oneside.tex ${MF}.tex
	@${MAKE} -s finalpdf

mono: clean export
	@${MAKE} -s MONO=yes clean-figs pdffigs
	@${MAKE} -s finalpdf

mono-oneside: clean export
	@${MAKE} -s MONO=yes clean-figs pdffigs
	@sed 's/\\documentclass\[\(.*\)\]{\(.*\)}/\\documentclass[\1,oneside]{\2}/' \
	      ${MF}.tex > ${MF}-oneside.tex
	@mv ${MF}-oneside.tex ${MF}.tex
	@${MAKE} -s finalpdf

html: ${HTML_DOC_FILES}

help:
	@echo "Makefile for LaTeX/LyX documents"
	@echo
	@echo "Available targets:"
	@echo
	@echo "  all            compile the DVI output (default)"
	@echo "  ps             compile the Postscript output"
	@echo "  pdf            compile the PDF output"
	@echo "  final          compile the final DVI output"
	@echo "  finalps        compile the final Postscript output"
	@echo "  finalpdf       compile the final PDF output"
	@echo "  figs,psfigs    compile/convert figures for PS output"
	@echo "  pdffigs        compile/convert figures for PDF output"
	@echo "  clean          clean the directory"
	@echo "  edit           start LyX editor for the main file"
	@echo "                 (when preparing PDF output)"
	@echo "  export         export LyX document to LaTeX file"
	@echo "  exportps       export LyX document to Postscript format (by LyX)"
	@echo "  exportpdf      export LyX document to PDF format (by LyX)"
	@echo "  exporttxt      export LyX document to TXT format (by LyX)"
	@echo "  backup         create a backup"
	@echo "  bib            run BiBTeX for the document"
	@echo "  view           run DVI viewer"
	@echo "  viewps         run Postscript viewer"
	@echo "  viewpdf        run PDF viewer"
	@echo "  viewfinal      run viewer on final DVI output"
	@echo "  viewfinalps    run viewer on final Postscript output"
	@echo "  viewfinalpdf   run viewer on final PDF output"
	@echo "  signal         update DVI viewer"
	@echo "  wc             calculate number of words in the document"
	@echo "  html           convert all RST TXT files to HTML"
	@echo ""
