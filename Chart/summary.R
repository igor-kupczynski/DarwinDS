library("reshape")
library("ggplot2")

prep.data.line <- function(x, fname) {
  innerM <- melt(x, id=c("test", "try", "outer"))
  innerC <- cast(innerM, test + outer ~ variable, c(mean, sd))
  write.table(innerC, file=fname, sep=",", row.names=F)
  innerC[c("test", "outer", "value_mean")]
}

prep.data.box <- function(x) {
  last.gen <- 10
  data <- subset(x, outer==last.gen, select=c('test', 'try', 'value'))
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
  c <- c + opts(axis.text.x=theme_text(angle=-45, hjust=0))
  c
}

optimal <- function(optimal, range) {
  opt <- c(optimal)
  outer <- 1:range
  df <- data.frame(outer=outer, value=opt)
  geom_line(aes(outer, value), data=df)
}


read.cmd.args <- function() {
  n1 <- commandArgs()
  n <- n1[length(n1)]
  tmp<-strsplit(n,",")
  args <- tmp[[1]]
  if (length(args) == 6) {
    return(args)
  }
  c(2, 4154.441453, "summary.csv", "summary_short.csv", "summary1.pdf", "summary2.pdf")
}

### MAIN ######################################################################
args <- read.cmd.args()
df <-read.csv(args[3], header=TRUE)
pdf(args[5])
data <- prep.data.line(df, args[4])
data.box <- prep.data.box(df)
c <- gen.plot.line(data)
opt <- as.numeric(args[2])
if (opt != 0) {
  c <- c + optimal(opt, data$outer[length(data$outer)])
}
print(c)
dev.off()
pdf(args[6])
c <- gen.plot.box(data.box)
print(c)
dev.off()
