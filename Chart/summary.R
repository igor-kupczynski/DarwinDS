library("reshape")
library("ggplot2")

prep.data.line <- function(x, fname) {
  innerM <- melt(x, id=c("test", "try", "outer"))
  innerC <- cast(innerM, test + outer ~ variable, c(mean, sd))
  write.table(innerC, file=fname, sep=",", row.names=F)
  innerC[c("test", "outer", "value_mean")]
}

prep.data.box <- function(x) {
  data <- subset(x, outer==10, select=c('test', 'try', 'value'))
  data
}

gen.plot.line <- function(data) {
  c <- ggplot(data)
  c <- c + geom_point(aes(outer, value_mean, colour=test))
  c <- c + geom_line(aes(outer, value_mean, colour=test))
  c <- c + opts(title="Test summary")
  c
}

gen.plot.box <- function(data) {
  c <- ggplot(data)
  c <- c + geom_boxplot(aes(test, value))
  c
}

read.cmd.args <- function() {
  n1 <- commandArgs()
  n <- n1[length(n1)]
  tmp<-strsplit(n,",")
  args <- tmp[[1]]
  if (length(args) == 4) {
    return(args)
  }
  c("summary.csv", "summary_short.csv", "summary1.pdf", "summary2.pdf")
}

### MAIN ######################################################################
args <- read.cmd.args()
df <-read.csv(args[1], header=TRUE)
pdf(args[3])
data <- prep.data.line(df, args[2])
data.box <- prep.data.box(df)
c <- gen.plot.line(data)
print(c)
dev.off()
pdf(args[4])
c <- gen.plot.box(data.box)
print(c)
dev.off()
