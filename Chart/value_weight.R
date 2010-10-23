library("reshape")
library("ggplot2")

prep.data <- function(outerIdx, x, g1, g2) {
  inner <- subset(x, outer==outerIdx & generation %in% c(0,10,20,30,40,50,60),
                  select=c("generation", g1, g2))
  inner$generation <- factor(inner$generation)
  inner
}

gen.plot <- function(outerIdx, data, g1, g2) {
  c <- ggplot(data, aes_string(x=g1, y=g2, colour="generation"))
  c <- c + geom_point()
  c <- c + opts(title=paste(g1, " x ", g2, ", ext.iter.=", outerIdx, sep=""))
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
  c("evolution_report.csv", "ind_util.pdf")
}

### MAIN ######################################################################
args <- read.cmd.args()
df <-read.csv(args[2], header=TRUE)
g1 <- colnames(df)[8]
g2 <- colnames(df)[11]
pdf(args[3])
for (outer.idx in levels(factor(df$outer))) {
  data <- prep.data(outer.idx, df, g1, g2)
  c <- gen.plot(outer.idx, data, g1, g2)
  print(c)
}
dev.off()
