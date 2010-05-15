library("reshape")
library("ggplot2")

prep.data <- function(outerIdx, x) {
  inner <- subset(x, outer==outerIdx, select=c("value_50.0", "weight_50.0", "good"))
  inner$good <- factor(inner$good)
  inner
}

gen.plot <- function(outerIdx, data) {
  c <- ggplot(data, aes(weight_50.0, value_50.0, colour=good, size=good))
  c <- c + geom_point() + scale_colour_manual(value = c("black", "red"))
  c <- c + opts(title=paste("DM Choice, outer=", outerIdx, sep=""))
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
  c("dm_report.csv", "dmchoice.pdf")
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
