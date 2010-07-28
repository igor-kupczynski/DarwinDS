%
%  $Id: dcsarticle.cls 920 2009-12-21 21:28:12Z sobaniec $
%

\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{dcsarticle}[2009/12/21 v1.1 DCS Group article class]
\DeclareOption*{\PassOptionsToClass{\CurrentOption}{article}}
\ProcessOptions\relax
\LoadClass{article}


\RequirePackage[a4paper]{geometry}
\RequirePackage[pagestyles,clearempty,psfloats]{titlesec}
\RequirePackage{dcslib}[2009/12/21]


% default page layout
\newlength{\bindinglen}
\setlength{\bindinglen}{0.5cm}
\geometry{tmargin=3cm,bmargin=3cm,lmargin=2.75cm,rmargin=2.75cm,
          bindingoffset=0cm}

% twoside
\DeclareOption{twoside}{
  \geometry{twoside=true,lmargin=2.5cm,rmargin=2.5cm,
            bindingoffset=\bindinglen}
}


% fonts
\newcommand{\secfont}{\sffamily}


% page styles
\newpagestyle{main}[\footnotesize\itshape]{
  \sethead{}{}{}
  \setfoot{}{\rm\normalsize\thepage}{}
  \setfloathead{}{}{}{\setheadrule{0pt}}[p]
}
\pagestyle{main}


% matters
\newif\if@mainmatter \@mainmattertrue
\newcommand\mainmatter{%
  \cleardoublepage
  \@mainmattertrue
  \pagenumbering{arabic}}
\newcommand\backmatter{%
  \if@openright
    \cleardoublepage
  \else
    \clearpage
  \fi
  \@mainmatterfalse}


% sectioning
\DeclareOption{sanssec}{
  \titleformat{\section}{\secfont\LARGE\filright}{\thesection}{1em}{}
  \titleformat{\subsection}{\secfont\Large\filright}{\thesubsection}{1em}{}
  \titleformat{\subsubsection}{\secfont\large\filright}{\thesubsubsection}{1em}{}
  \titleformat{\subparagraph}[runin]{\em}{}{0em}{}
  \titlespacing{\subparagraph}{0pt}{\bigskipamount}{1em}
}

\DeclareOption{sanssecsmall}{
  \titleformat{\section}{\secfont\Large\filright}{\thesection}{1em}{}
  \titleformat{\subsection}{\secfont\large\filright}{\thesubsection}{1em}{}
  \titleformat{\subsubsection}{\secfont\filright}{\thesubsubsection}{1em}{}
  \titleformat{\subparagraph}[runin]{\em}{}{0em}{}
  \titlespacing{\subparagraph}{0pt}{\bigskipamount}{1em}
}

% report option
\DeclareOption{report}{
  \renewcommand{\and}{\par}
  \renewcommand{\maketitle}{%
    \begin{titlepage}
    \secfont
    \setlength{\parindent}{0pt}
    \null\vfill
    \hfill\begin{minipage}{12.5cm}
      \begin{center}
        {\fontsize{18}{20}\selectfont\@title\par}
        \fontsize{12}{14}\selectfont
        \vspace{5mm}
        {\@author}\par
        \vspace{5mm}
        {\fontsize{10}{12}\selectfont \techreportname\ \@date}
      \end{center}
    \end{minipage}\hspace{\bindinglen}\hfill\null
    \vfill\null
    \end{titlepage}
    \cleardoublepage
  }

  \renewcommand\tableofcontents{%
    \section*{\contentsname}
    \pagestyle{empty}
    \@starttoc{toc}
    \cleardoublepage
    \pagestyle{main}}

  % page layout
  \geometry{tmargin=3.5cm,bmargin=3cm,lmargin=2.75cm,rmargin=2.75cm,
            headsep=7ex,footskip=0cm,bindingoffset=\bindinglen}

  % line space
  \setstretch{1.1}

  \ExecuteOptions{sanssec}

  \setlength{\parindent}{1pc}
 
  % parskip
  \setlength{\parskip}{0.6ex}

  % page styles
  \renewpagestyle{plain}{
    \setfoot{}{}{}
  }
  \renewpagestyle{main}[\footnotesize\itshape]{
    \headrule
    \sethead[\rm\normalsize\thepage][]%
            [\if@mainmatter
               \thesection~~\sectiontitle
             \else
               \sectiontitle
             \fi]%
            {\if@mainmatter
               \thesection~~\sectiontitle
             \else
               \sectiontitle
             \fi}{}{\rm\normalsize\thepage}
    \setfloathead{}{}{}{\setheadrule{0pt}}[p]
  }
  \pagestyle{main}

  % abstract environment
  \renewenvironment{abstract}
    {
     \thispagestyle{plain}
     \mbox{}\vfill
     \begin{list}{}{
       \setlength{\leftmargin}{0.1\textwidth}
       \setlength{\rightmargin}{0.1\textwidth}
       \begin{center}
         \secfont\large\abstractname
       \end{center}
       }
       \item\relax
    }
    {\end{list}\vfill\vfill\vfill\mbox{}
     \cleardoublepage
    }

}


% default colors
\lstset{language=c}
\lstset{keywordstyle=\bfseries\color{dkgreen},
        commentstyle=\itshape\color{dkblue},
        stringstyle=\color{dkred},
        directivestyle=\color{directive}}
\lstset{language=}


% B&W
\DeclareOption{bw}{
  \lstset{language=c}
  \lstset{keywordstyle=\bfseries,commentstyle=\itshape,
          stringstyle=,directivestyle=}
  \lstset{language=}
  \definecolor{directive}{rgb}{0, 0, 0}
  \definecolor{linkcolor}{rgb}{0, 0, 0}
  \definecolor{pathcolor}{rgb}{0, 0, 0}
  \definecolor{namecolor}{rgb}{0, 0, 0}
  \definecolor{cmdcolor}{rgb}{0, 0, 0}
  \definecolor{mancolor}{rgb}{0, 0, 0}
  \definecolor{codecolor}{rgb}{0, 0, 0}
}


% lists spacing
\def\enumerate{%
  \ifnum \@enumdepth >\thr@@\@toodeep\else
    \advance\@enumdepth\@ne
    \edef\@enumctr{enum\romannumeral\the\@enumdepth}%
      \expandafter
      \list
        \csname label\@enumctr\endcsname
        {\usecounter\@enumctr\def\makelabel##1{\hss\llap{##1}}}%
        \if@firmlists\firmlist\fi%
  \fi}

\def\itemize{%
  \ifnum \@itemdepth >\thr@@\@toodeep\else
    \advance\@itemdepth\@ne
    \edef\@itemitem{labelitem\romannumeral\the\@itemdepth}%
    \expandafter
    \list
      \csname\@itemitem\endcsname
      {\def\makelabel##1{\hss\llap{##1}}}%
      \if@firmlists\firmlist\fi%
  \fi}

\renewenvironment{description}
  {\list{}{\labelwidth\z@ \itemindent-\leftmargin
           \let\makelabel\descriptionlabel}%
           \if@firmlists\firmlist\fi}
  {\endlist}

\ProcessOptions\relax
