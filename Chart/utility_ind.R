library("reshape")
library("ggplot2")

prep.data <- function(outerIdx, x) {
  inner <- subset(x, outer==outerIdx & generation %in% c(0,10,20,30,40,50,60),
                  select=c("generation", "utility"))
  inner$generation <- factor(inner$generation)
  inner <- inner[with(inner, order(generation, utility)), ]
  inner$ind <- 1:summary(inner$generation)[1]
  inner
}

gen.plot <- function(outerIdx, data) {
  c <- ggplot(data, aes(ind, utility, colour=generation))
  c <- c + geom_point() + geom_line()
  c <- c + opts(title=paste("Utility/Individual, outer=", outerIdx, sep=""))
  c
}

read.cmd.args <- function() {
  n1 <- commandArgs()
  n <- n1[length(n1)]
  tmp<-strsplit(n,",")
  args <- tmp[[1]]
  if (length(args) == 2) {
    return(args)
  }
  c("evolution_report.csv", "utilind.pdf")
}

### MAIN ######################################################################
args <- read.cmd.args()
df <-read.csv(args[1], header=TRUE)
pdf(args[2])
for (outer.idx in levels(factor(df$outer))) {
  data <- prep.data(outer.idx, df)
  c <- gen.plot(outer.idx, data)
  print(c)
}
dev.off()
