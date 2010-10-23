library("reshape")
library("ggplot2")

prep.data.line <- function(x, fname) {
  innerM <- melt(x, id=c("test", "try", "exterior_iteration"))
  innerC <- cast(innerM, test + exterior_iteration ~ variable, c(mean, sd))
  innerC[c("test", "exterior_iteration", "utility_mean")]
}

gen.plot.line <- function(data,chart.title) {
  c <- ggplot(data)
  c <- c + geom_point(aes(exterior_iteration, utility_mean), colour="red")
  c <- c + geom_line(aes(exterior_iteration, utility_mean), colour="red")
  c <- c + opts(title=chart.title)
  c
}

optimal <- function(optimal, range) {
  opt <- c(optimal)
  exterior_iteration <- 1:range
  df <- data.frame(exterior_iteration=exterior_iteration, utility=opt)
  geom_line(aes(exterior_iteration, utility), data=df)
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
df <- rename(df, c(outer="exterior_iteration", value="utility"))
pdf(args[5])
data <- prep.data.line(df, args[3])
write.csv(data, "summary_mean.csv")
c <- gen.plot.line(data, args[3])
opt <- as.numeric(args[2])
if (opt != 0) {
  c <- c + optimal(opt, data$exterior_iteration[length(data$exterior_iteration)])
}
print(c)
dev.off()
