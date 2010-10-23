library("reshape")
library("ggplot2")

prep.data.line <- function(x, fname) {
  innerM <- melt(x, id=c("test", "try", "exterior_iteration"))
  innerC <- cast(innerM, test + exterior_iteration ~ variable, c(mean, sd))
  write.table(innerC, file=fname, sep=",", row.names=F)
  innerC[c("test", "exterior_iteration", "utility_mean")]
}

prep.data.box <- function(x) {
  last.gen <- 10
  data <- subset(x, exterior_iteration==last.gen, select=c('test', 'try', 'utility'))
  data
}

gen.plot.line <- function(data) {
  c <- ggplot(data)
  c <- c + geom_point(aes(exterior_iteration, utility_mean, colour=test))
  c <- c + geom_line(aes(exterior_iteration, utility_mean, colour=test))
  c <- c + opts(title="Test summary")
  c
}

gen.plot.box <- function(data) {
  c <- ggplot(data)
  c <- c + geom_boxplot(aes(test, utility))
  c <- c + opts(axis.text.x=theme_text(angle=-45, hjust=0))
  c <- c + opts(title="Test summary, ext.iter.=10")
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
  if (length(args) == 6) {
    return(args)
  }
  c(2, 4154.441453, "summary.csv", "summary_short.csv", "summary1.pdf", "summary2.pdf")
}

### MAIN ######################################################################
args <- read.cmd.args()
df <-read.csv(args[3], header=TRUE)
df <- rename(df, c(outer="exterior_iteration", value="utility"))
pdf(args[5])
data <- prep.data.line(df, args[4])
data.box <- prep.data.box(df)
c <- gen.plot.line(data)
opt <- as.numeric(args[2])
if (opt != 0) {
  c <- c + optimal(opt, data$outer[length(data$exterior_iteration)])
}
print(c)
dev.off()
pdf(args[6])
c <- gen.plot.box(data.box)
print(c)
dev.off()
