library("reshape")
library("ggplot2")

get.inner <- function(outerIdx, x) {
  inner <- subset(x, outer==outerIdx & generation %in% c(0,10,20,30,40,50,60),
                  select=c("generation", "utility", "primary"))
  inner
}

prep.data.util <- function(x) {
  inner <- x[c("generation", "utility")]
  inner$generation <- factor(inner$generation)
  inner <- inner[with(inner, order(generation, utility)), ]
  inner$individual <- 1:summary(inner$generation)[1]
  inner
}

prep.data.ps <- function(x) {
  inner <- x[c("generation", "primary")]
  inner$generation <- factor(inner$generation)
  inner <- inner[with(inner, order(generation, primary)), ]
  inner$individual <- 1:summary(inner$generation)[1]
  inner
}

gen.plot.util <- function(outerIdx, data) {
  c <- ggplot(data, aes(individual, utility, colour=generation))
  c <- c + geom_point() + geom_line()
  c <- c + opts(title=paste("Utility/Individual, ext.iter.=", outerIdx, sep=""))
  c
}

gen.plot.ps <- function(outerIdx, data) {
  c <- ggplot(data, aes(individual, primary, colour=generation))
  c <- c + geom_point() + geom_line()
  c <- c + opts(title=paste("Primary/Individual, ext.iter.=", outerIdx, sep=""))
  c
}

read.cmd.args <- function() {
  n1 <- commandArgs()
  n <- n1[length(n1)]
  tmp<-strsplit(n,",")
  args <- tmp[[1]]
  if (length(args) == 3) {
    return(args)
  }
  c(2, "evolution_report.csv", "utilind.pdf")
}

### MAIN ######################################################################
args <- read.cmd.args()
df <-read.csv(args[2], header=TRUE)
pdf(args[3])
for (outer.idx in levels(factor(df$outer))) {
  grid.newpage()
  pushViewport(viewport(layout = grid.layout(2, 1)))
  inner <- get.inner(outer.idx, df)
  data <- prep.data.util(inner)
  c <- gen.plot.util(outer.idx, data)
  print(c, vp=viewport(layout.pos.row = 1, layout.pos.col = 1))
  data <- prep.data.ps(inner)
  c <- gen.plot.ps(outer.idx, data)
  print(c, vp=viewport(layout.pos.row = 2, layout.pos.col = 1))
}
dev.off()
