library("reshape")
library("ggplot2")

get.inner <- function(outerIdx, x) {
  inner <- subset(df, outer==outerIdx,
                  select=c("generation", "rank", "utility", "primary"))
  inner
}

prep.data.util <- function(x) {
  inner <- inner[c("generation", "rank", "utility")]
  innerM <- melt(inner, c("generation", "rank"))
  innerC <- cast(innerM, generation ~ variable, c(mean, min, max))
  innerC
}

prep.data.ps <- function(x) {
  inner <- inner[c("generation", "rank", "primary")]
  innerM <- melt(inner, c("generation", "rank"))
  innerC <- cast(innerM, generation ~ variable, c(mean, min, max))
  innerC
}

gen.plot.util <- function(outerIdx, data) {
  dataM <- melt(data, id=c("generation"))
  c <- ggplot(dataM)
  c <- c + geom_ribbon(aes(generation, ymin=utility_min, ymax=utility_max),
                       data=data, alpha=0.2)
  c <- c + geom_point(aes(generation, value, colour=result_variable))
  c <- c +  geom_line(aes(generation, value, colour=result_variable))
  c <- c + opts(title=paste("UtilGen, ext.iter.=", outerIdx, sep=""))
  c
}

gen.plot.ps <- function(outerIdx, data) {
  dataM <- melt(data, id=c("generation"))
  c <- ggplot(dataM)
  c <- c + geom_ribbon(aes(generation, ymin=primary_min, ymax=primary_max),
                       data=data, alpha=0.2)
  c <- c + geom_point(aes(generation, value, colour=result_variable))
  c <- c +  geom_line(aes(generation, value, colour=result_variable))
  c <- c + opts(title=paste("PrimaryScoreGen, ext.iter.=", outerIdx, sep=""))
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
  c(2, "evolution_report.csv", "utilgen.pdf")
}

### MAIN ######################################################################
args <- read.cmd.args()
df <-read.csv(args[2], header=TRUE)
pdf(args[3])
for (outer.idx in levels(factor(df$outer))) {
  grid.newpage()
  pushViewport(viewport(layout = grid.layout(2, 1)))
  inner <- get.inner(outer.idx, df)
  data <- prep.data.util(inner)
  c <- gen.plot.util(outer.idx, data)
  print(c, vp=viewport(layout.pos.row = 1, layout.pos.col = 1))
  data <- prep.data.ps(inner)
  c <- gen.plot.ps(outer.idx, data)
  print(c, vp=viewport(layout.pos.row = 2, layout.pos.col = 1))
}
dev.off()

