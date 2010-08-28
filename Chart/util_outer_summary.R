library("reshape")
library("ggplot2")

prep.data.line <- function(x, fname) {
  innerM <- melt(x, id=c("test", "try", "outer"))
  innerC <- cast(innerM, test + outer ~ variable, c(mean, sd))
  innerC[c("test", "outer", "value_mean")]
}

gen.plot.line <- function(data,chart.title) {
  c <- ggplot(data)
  c <- c + geom_point(aes(outer, value_mean), colour="red")
  c <- c + geom_line(aes(outer, value_mean), colour="red")
  c <- c + opts(title=chart.title)
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
  if (length(args) == 5) {
    return(args)
  }
  c(2, 4154.441453, "Pro-chart", "summary.csv", "util-outer-summary.pdf")
}

### MAIN ######################################################################
args <- read.cmd.args()
df <-read.csv(args[4], header=TRUE)
pdf(args[5])
data <- prep.data.line(df, args[3])
c <- gen.plot.line(data, args[3])
opt <- as.numeric(args[2])
if (opt != 0) {
  c <- c + optimal(opt, data$outer[length(data$outer)])
}
print(c)
dev.off()
