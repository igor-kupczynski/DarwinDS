library("reshape")
library("ggplot2")

prep.data <- function(outerIdx, x) {
  inner <- subset(df, outer==outerIdx)
  inner <- inner[c("generation", "rank", "utility")]
  innerM <- melt(inner, c("generation", "rank"))
  innerC <- cast(innerM, generation ~ variable, c(mean, min, max))
  innerC
}

gen.plot <- function(outerIdx, data) {
  dataM <- melt(data, id=c("generation"))
  c <- ggplot(dataM)
  c <- c + geom_ribbon(aes(generation, ymin=utility_min, ymax=utility_max),
                       data=data, alpha=0.2)
  c <- c + geom_point(aes(generation, value, colour=result_variable))
  c <- c +  geom_line(aes(generation, value, colour=result_variable))
  c <- c + opts(title=paste("UtilGen, outer=", outerIdx, sep=""))
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
  c("evolution_report.csv", "utilgen.pdf")
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
