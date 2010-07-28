%
%  $Id: dcsbook.cls 1003 2010-01-08 10:41:52Z sobaniec $
%

\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{dcsbook}[2009/12/21 v1.1 DCS Group book class]
\DeclareOption*{\PassOptionsToClass{\CurrentOption}{book}}
\ProcessOptions\relax
\LoadClass{book}


\RequirePackage[a4paper]{geometry}
\RequirePackage[pagestyles,clearempty,psfloats]{titlesec}
\RequirePackage{titletoc}
\RequirePackage{dcslib}[2009/12/21]
\RequirePackage[nottoc,notlot,notlof]{tocbibind}
\RequirePackage{calc}
\RequirePackage{translator}

\usedictionary{translator-thesis-dictionary}

% default page layout
\geometry{twoside=true,
          tmargin=3cm,bmargin=3cm,lmargin=2.5cm,rmargin=2.5cm,
          headsep=30pt,footskip=0cm,
          bindingoffset=1cm}

% oneside
\DeclareOption{oneside}{
  \geometry{twoside=false,lmargin=3cm,rmargin=3cm,bindingoffset=0cm}
}


% fonts
\newcommand{\secfont}{\fontfamily{pag}\selectfont}
\newcommand{\headingfont}{\small\itshape}
\newcommand{\ttfont}{\small\ttfamily}


% front/back matters
\renewcommand\frontmatter{%
  \cleardoublepage
  \@mainmatterfalse
  \pagenumbering{arabic}}
\renewcommand\mainmatter{%
  \cleardoublepage
  \@mainmattertrue}


% page styles
\newpagestyle{main}{
  \sethead[\usepage][]%
  [\footnotesize\itshape
    \if@mainmatter
      \ifthenelse{\value{chapter}>0}{\thechapter~~}{}
    \fi
    \chaptertitle]%
  {\footnotesize\itshape
    \if@mainmatter
      \ifthesection{\thesection~~\sectiontitle}%
                   {\ifthenelse{\value{chapter}>0}{\thechapter~~}{}\chaptertitle}
    \else
      \chaptertitle
    \fi
  }{}{\usepage}
  \setfoot{}{}{}
  \setfloathead{}{}{}{}[p]
}
\renewpagestyle{plain}{
  \setfoot{}{}{}
}
\pagestyle{main}


% url
\def\UrlFont{\sffamily}

% Metadata:
% supervisor
\newcommand{\@supervisorname}
  {\textcolor{red}{\texttt{\small Unknown. Use \textbackslash{}supervisor command.}}}
\newcommand{\supervisor}[1]{\renewcommand{\@supervisorname}{#1}}
% subtitle
\newcommand{\@subtitle}{\translate{Master's Thesis}}
\newcommand{\subtitle}[1]{\renewcommand{\@subtitle}{#1}}
% university
\newcommand{\@university}{\translate{Pozna\'n University of Technology}}
\newcommand{\university}[1]{\renewcommand{\@university}{#1}}
% institute
\newcommand{\@institute}{\translate{Institute of Computing Science}}
\newcommand{\institute}[1]{\renewcommand{\@institute}{#1}}

\renewcommand{\and}{\\}
\newenvironment{abstract}{
  \chapter*{\abstractname}
  %\addcontentsline{toc}{section}{\abstractname}
}{}


\DeclareOption{dcstoc}{
  % titletoc
  \newlength{\@chaptertocsep}
  \setlength{\@chaptertocsep}{1.9em}
  \newlength{\@sectiontocsep}
  \setlength{\@sectiontocsep}{2.5em}
  \newlength{\@subsectiontocsep}
  \setlength{\@subsectiontocsep}{3.2em}
  \titlecontents{part}[2em]{\vspace{2.2ex}}%
    {\flushleft\secfont\bfseries\Large\contentslabel{1.5em}}{\hspace*{-1.5em}}%
    {~\hfill\Large\bfseries\contentspage}[\vspace{0.5ex}]
  \titlecontents{chapter}[\@chaptertocsep]{\vspace{1.5ex}}%
    {\secfont\bfseries\contentslabel{\@chaptertocsep}}{\secfont\bfseries\hspace*{-\@chaptertocsep}}%
    {~\hfill\large\bfseries\contentspage}[\vspace{0.3ex}]
  \titlecontents{section}[\@chaptertocsep+\@sectiontocsep]{}%
    {\contentslabel{\@sectiontocsep}}{\hspace*{-\@sectiontocsep}}%
    {\titlerule*[1pc]{.}\contentspage}
  \titlecontents{subsection}[\@chaptertocsep+\@sectiontocsep+\@subsectiontocsep]{}%
    {\contentslabel{\@subsectiontocsep}}{\hspace*{-\@subsectiontocsep}}%
    {\titlerule*[1pc]{.}\contentspage}
}

\newcommand{\@institution}{Politechnika Pozna\'nska\\Instytut Informatyki}
\newcommand{\institution}[1]{\renewcommand{\@institution}{#1}}

\DeclareOption{textbook}{
  \renewcommand{\maketitle}{%
    \begin{titlepage}
    \fontfamily{pag}\selectfont
    \setlength{\parindent}{0pt}
    \parbox[b]{0.9\textwidth}{\bfseries\huge\raggedright\textcolor{dkgray}{\@title}}
    \vspace{1pt}\par
    \colorbox{gray}{\parbox{\textwidth}{
      \raggedleft
      \textcolor{white}{\bfseries \@date~}}}

    \vspace{0.10\textheight}
    \hfill\parbox{0.95\textwidth}{\raggedleft\large\@author}

    \vfill
    \begin{center}
    \rule{0.5\textwidth}{0.5pt}\\
    \scriptsize
    \@institution
    \end{center}
    \end{titlepage}
    \cleardoublepage
  }

  % titlesec
  \titleformat{\part}{\secfont\Large\bfseries}{\thepart}{1em}{}
  \titleformat{\chapter}[block]{\filleft\secfont\Huge}{}{0em}%
      {\parbox{0.9\textwidth}%
      {\raggedleft\fontfamily{phv}\fontsize{65}{50}\selectfont%
       \textcolor{gray}{\thechapter}}\vspace{0.5ex}\\}
  \titleformat{name=\chapter,numberless}[block]{\secfont\Huge\filleft}{}{0em}{}
  \titlespacing*{\chapter}{0pt}{0cm}{4cm}
  \titleformat{\section}{\secfont\Large\bfseries\filright}{\thesection}{1em}{}
  \titleformat{\subsection}{\secfont\large\bfseries\filright}{\thesubsection}{1em}{}
  \titleformat{\subsubsection}{\secfont\bfseries\filright}{\thesubsubsection}{1em}{}
  \titleformat{\paragraph}[runin]{\bfseries}{}{0em}{}
  \titlespacing{\paragraph}{0pt}{3.5ex plus 0.6ex minus 0.2ex}{1em}

  \ExecuteOptions{dcstoc}
}


% Master's Thesis
\DeclareOption{thesis}{
  % line skip
  \setstretch{1.1}

  \renewcommand{\maketitle}{%
    \begin{titlepage}
    \begin{center}
    {\large\@university\par
    \@institute}\par
    \vspace{0.7cm}
    \includegraphics[width=3cm]{logopp}
    \end{center}

    \vfill
    \begin{center}
    {\large\secfont\@author}\par
    \vspace{0.7cm}
    {\huge\secfont\begin{spacing}{1}\@title\end{spacing}}\par
    \vspace{0.7cm}
    {\large\@subtitle}\par
    \end{center}
    \vfill\vfill\vfill\vfill

    \begin{center}
    {\large \translate{Supervisor}:~ \@supervisorname}\par
    {\smallskip\large \@date}
    \end{center}

    \end{titlepage}
    \cleardoublepage
  }

  % titlesec
  \titleformat{\part}{\secfont\Large\bfseries}{\thepart}{1em}{}
  \titleformat{\chapter}[display]{\normalfont\filcenter\secfont}
    {\Large\hrulefill~~\chaptertitlename~\thechapter~~\hrulefill}{0pc}{\Huge}
  \titleformat{name=\chapter,numberless}[display]{\normalfont\filcenter\secfont}
    {}{0.5pc}{\Huge}
  \titlespacing*{\chapter}{0pt}{0cm}{4cm}
  \titleformat{\section}{\secfont\Large\bfseries\filright}{\thesection}{1em}{}
  \titleformat{\subsection}{\secfont\large\bfseries\filright}{\thesubsection}{1em}{}
  \titleformat{\subsubsection}{\secfont\bfseries\filright}{\thesubsubsection}{1em}{}
  \titleformat{\paragraph}[runin]{\bfseries}{}{0em}{}
  \titlespacing{\paragraph}{0pt}{3.5ex plus 0.6ex minus 0.2ex}{1em}

  \renewenvironment{abstract}
    {
     \cleardoublepage
     \thispagestyle{plain}
     \mbox{}\vfill
     \begin{list}{}{
       \setlength{\leftmargin}{0.1\textwidth}
       \setlength{\rightmargin}{0.1\textwidth}
       \begin{center}
         \secfont\Large\abstractname
       \end{center}
       \smallskip
       \small
       \setstretch{1}
     }
     \item\relax
    }
    {\end{list}\vfill\vfill\vfill\mbox{}}

    \ExecuteOptions{dcstoc}
}

\DeclareOption{msc}{
  \@latex@error{Instead of "msc" use "thesis" option}
}

% Technical reports
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
    \end{minipage}\hspace{0.5cm}\hfill\null
    \vfill\null
    \end{titlepage}
    \cleardoublepage
  }

  \renewcommand\tableofcontents{%
    \chapter*{\contentsname}
    \pagestyle{empty}
    \@starttoc{toc}
    \cleardoublepage
    \pagestyle{main}}

  % page layout
  \geometry{tmargin=3.5cm,bmargin=3cm,lmargin=2.75cm,rmargin=2.75cm,
            headsep=7ex,footskip=0cm,bindingoffset=0.5cm}

  % page styles
  \renewpagestyle{main}[\footnotesize\itshape]{
    \headrule
    \sethead[\rm\usepage][]%
    [\if@mainmatter
       \ifthenelse{\value{chapter}>0}{\thechapter~~}{}
     \fi
     \chaptertitle]%
    {\if@mainmatter
       \ifthesection{\thesection~~\sectiontitle}%
                    {\ifthenelse{\value{chapter}>0}{\thechapter~~}{}\chaptertitle}
     \else
       \chaptertitle
     \fi
    }{}{\rm\usepage}
    \setfoot{}{}{}
    \setfloathead{}{}{}{\setheadrule{0pt}}[p]
  }
  \pagestyle{main}

  % line space
  \setstretch{1.1}

  % titlesec
  \titleformat{\chapter}[block]{\filright\secfont\Huge}{}{0em}%
      {\textcolor{midgray}{\LARGE\@chapapp~\thechapter}\\}
  \titleformat{name=\chapter,numberless}[block]{\secfont\Huge\filright}{}{0em}{}
  \titlespacing*{\chapter}{0pt}{0cm}{4cm}

  \titleformat{\section}{\secfont\LARGE\filright}{\thesection}{1em}{}
  \titleformat{\subsection}{\secfont\Large\filright}{\thesubsection}{1em}{}
  \titleformat{\subsubsection}{\secfont\large\filright}{\thesubsubsection}{1em}{}
  \titleformat{\subparagraph}[runin]{\em}{}{0em}{}
  \titlespacing{\subparagraph}{0pt}{\bigskipamount}{1em}

  \ExecuteOptions{dcstoc}

  \setlength{\parindent}{1pc}

  % parskip
  \setlength{\parskip}{0.6ex}

  % abstract environment
  \renewenvironment{abstract}
    {
     \thispagestyle{plain}
     \mbox{}\vfill
     \begin{list}{}{
       \setlength{\leftmargin}{0.1\textwidth}
       \setlength{\rightmargin}{0.1\textwidth}
       \begin{center}
         \secfont\Large\abstractname
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
