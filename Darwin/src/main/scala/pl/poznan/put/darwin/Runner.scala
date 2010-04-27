package pl.poznan.put.darwin.model

import pl.poznan.put.darwin.simulation.Simulation
import pl.poznan.put.darwin.model.problem.{Parser, Problem}
import org.ini4j.ConfigParser
  
object Runner {
  def main(args: Array[String]) {

    val problemFilename = "etc/problems/simple_1crit.mod"
    val configFilename = "etc/config.ini"

    val parser = new ConfigParser()
    parser.read(configFilename)
  
    val config = new Config(parser)

    val lines = io.Source.fromPath(problemFilename).mkString
    val problem: Problem = Parser.fromText(lines)

    (new Simulation(config, problem)).run()
  }
}

