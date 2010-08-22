library("reshape")
library("ggplot2")

prep.data <- function(outerIdx, x, g1, g2) {
  inner <- subset(x, outer==outerIdx, select=c(g1, g2, "good"))
  inner$good <- factor(inner$good)
  inner
}

gen.plot <- function(outerIdx, data, g1, g2) {
  c <- ggplot(data, aes_string(x=g1, y=g2, colour="good", size="good"))
  c <- c + geom_point() + scale_colour_manual(value = c("black", "red"))
  c <- c + opts(title=paste("DM Choice, outer=", outerIdx, sep=""))
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
  c(2, "dm_report.csv", "dmchoice.pdf")
}

### MAIN ######################################################################
args <- read.cmd.args()
if (args[1] == 2) {
  df <-read.csv(args[2], header=TRUE)
  g1 <- colnames(df)[6]
  g2 <- colnames(df)[9]
  pdf(args[3])
  for (outer.idx in levels(factor(df$outer))) {
    data <- prep.data(outer.idx, df, g1, g2)
    c <- gen.plot(outer.idx, data, g1, g2)
    print(c)
  }
dev.off()
} else {
  cat(sprintf("Only works for two criteria, selected %d\n", args[0]))
}
