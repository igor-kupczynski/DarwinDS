library("reshape")
library("ggplot2")

get.inner <- function(outerIdx, x, x.name, y, y.name) {
  inner1 <- subset(x, outer==outerIdx,
                  select=c("generation", "rank", "utility"))
  colnames(inner1) <- c("generation", "rank", x.name)
  inner2 <- subset(y, outer==outerIdx,
                  select=c("generation", "rank", "utility"))
  colnames(inner2) <- c("generation", "rank", y.name)
  inner <- merge(inner1, inner2)
}

best <- max

prep.data.util <- function(x, x.name, y.name) {
  inner <- x[c("generation", "rank", x.name, y.name)]
  innerM <- melt(inner, c("generation", "rank"))
  innerC <- cast(innerM, generation ~ variable, c(mean, best))
  innerC
}

gen.plot.util <- function(outerIdx, data) {
  dataM <- melt(data, id=c("generation"))
  dataM$method <- paste(dataM$variable, dataM$result_variable, sep="_")
  c <- ggplot(dataM)
  c <- c + geom_point(aes(generation, value, colour=method))
  c <- c +  geom_line(aes(generation, value, colour=method))
  c <- c + opts(title=paste("UtilGenComparison, outer=", outerIdx, sep=""))
  c
}

read.cmd.args <- function() {
  n1 <- commandArgs()
  n <- n1[length(n1)]
  tmp<-strsplit(n,",")
  args <- tmp[[1]]
  if (length(args) == 5) {
    return(args)
  }
  c("evolution_report.csv", "1", "../../0/reports/evolution_report.csv", "2", "comparison.pdf")
}

### MAIN ######################################################################
args <- read.cmd.args()
df1 <-read.csv(args[1], header=TRUE)
df2 <-read.csv(args[3], header=TRUE)
pdf(args[5])
for (outer.idx in levels(factor(df1$outer))) {
  inner <- get.inner(outer.idx, df1, args[2], df2, args[4])
  data <- prep.data.util(inner, args[2], args[4])
  c <- gen.plot.util(outer.idx, data)
  print(c)
}
dev.off()
