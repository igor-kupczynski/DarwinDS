library("reshape")
library("ggplot2")

last <- function(x) {
  x[length(x)]
}

prep.data <- function(x) {
  inner <- df[c("outer", "generation", "rank", "utility")]
  innerM <- melt(inner, id=c("outer", "generation", "rank"))
  innerC <- cast(innerM, outer + generation ~ variable, c(min, max))
  innerM <- melt(innerC, id=c("outer", "generation"))
  innerC <- cast(innerM, outer ~ variable, c(min, max, last))
  innerC
}

gen.plot <- function(data) {
  dataM <- melt(data, id=c("outer"))
  c <- ggplot(dataM)
  c <- c + geom_ribbon(aes(outer, ymin=utility_min, ymax=utility_max),
                       data=data, alpha=0.2)
  c <- c + geom_point(aes(outer, value, colour=result_variable))
  c <- c + geom_line(aes(outer, value, colour=result_variable))
  c <- c + opts(title="Utility/Outer")
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
  c("evolution_report.csv", "utilouter.pdf")
}

### MAIN ######################################################################
args <- read.cmd.args()
df <-read.csv(args[1], header=TRUE)
pdf(args[2])
data <- prep.data(df)
c <- gen.plot(data)
print(c)
dev.off()
