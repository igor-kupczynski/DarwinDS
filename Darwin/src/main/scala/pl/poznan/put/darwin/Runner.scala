package pl.poznan.put.darwin.model

import pl.poznan.put.darwin.simulation.Simulation
import pl.poznan.put.darwin.model.problem.{Parser, Problem}
  
object Runner {
  def main(args: Array[String]) {
    val config = new Config()
    val lines = io.Source.fromPath(config.FILENAME).mkString
    val problem: Problem = Parser.fromText(lines)
    (new Simulation(config, problem)).run()
  }
}

