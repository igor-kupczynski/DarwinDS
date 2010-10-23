library("reshape")
library("ggplot2")

last <- function(x) {
  x[length(x)]
}


prep.data <- function(x) {
  inner <- df[c("exterior_iteration", "generation", "rank", "utility")]
  innerM <- melt(inner, id=c("exterior_iteration", "generation", "rank"))
  innerC <- cast(innerM, exterior_iteration + generation ~ variable, c(min, max))
  innerM <- melt(innerC, id=c("exterior_iteration", "generation"))
  innerC <- cast(innerM, exterior_iteration ~ variable, c(min, max))
  innerC
}

add.optimal <- function(plot, optimal, range) {
  opt <- c(optimal)
  exterior_iteration <- 1:range
  df <- data.frame(exterior_iteration=exterior_iteration, value=opt)
  plot <- plot +  geom_line(aes(exterior_iteration, value), data=df)
  plot
}

gen.plot <- function(data, filename) {
  dataM <- melt(data, id=c("exterior_iteration"))
  write.table(data, file=filename, sep=",", row.names=F)
  dataM$result_value <- dataM$result_variable
  c <- ggplot(dataM)
  c <- c + geom_ribbon(aes(exterior_iteration, ymin=utility_min, ymax=utility_max),
                       data=data, alpha=0.2)
  c <- c + geom_point(aes(exterior_iteration, value, colour=result_value))
  c <- c + geom_line(aes(exterior_iteration, value, colour=result_value))
  c <- c + opts(title="Supposed Utility")
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
df <- rename(df, c(outer="exterior_iteration"))
pdf(args[4])
data <- prep.data(df)
c <- gen.plot(data, args[5])
opt <- as.numeric(args[2])
if (opt != 0) {
  c <- add.optimal(c, opt, length(data$exterior_iteration))
}
print(c)
dev.off()
