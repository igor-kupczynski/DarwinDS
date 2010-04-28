package pl.poznan.put.darwin.model

import pl.poznan.put.darwin.simulation.Simulation
import pl.poznan.put.darwin.model.problem.{Parser, Problem}
import org.ini4j.ConfigParser
  
class Runner(problemFilename: String, configFilename: String) {
  
  def run() {

    val parser = new ConfigParser()
    parser.read(configFilename)
  
    val config = new Config(parser)

    val lines = io.Source.fromPath(problemFilename).mkString
    val problem: Problem = Parser.fromText(lines)

    (new Simulation(config, problem)).run()
    println(problemFilename)
  }
}

object Runner {

  def getArg(args: Array[String], name:String, default: String): String = {
    for (idx <- 0 to args.length-1) {
      if (args(idx) == name && idx+1 <= args.length-1)
        return args(idx+1)
    }
    return default
  }
  
  def main(args: Array[String]) {
    (new Runner(
      getArg(args, "--problem", "etc/problems/simple_1crit.mod"),
      getArg(args, "--config", "etc/config.ini")
      )
    ).run()
  }
}
