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

add.optimal <- function(plot, optimal, range) {
  opt <- c(optimal)
  outer <- 1:range
  df <- data.frame(outer=outer, value=opt)
  plot <- plot +  geom_line(aes(outer, value), data=df)
  plot
}

gen.plot <- function(data, filename) {
  dataM <- melt(data, id=c("outer"))
  write.table(data, file=filename, sep=",", row.names=F)
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
  if (length(args) == 5) {
    return(args)
  }
  c(2, 4154.441453, "evolution_report.csv", "utilouter.pdf", "outer.csv")
}

### MAIN ######################################################################
args <- read.cmd.args()

df <-read.csv(args[3], header=TRUE)
pdf(args[4])
data <- prep.data(df)
c <- gen.plot(data, args[5])
opt <- as.numeric(args[2])
if (opt != 0) {
  c <- add.optimal(c, opt, length(data$outer))
}
print(c)
dev.off()
