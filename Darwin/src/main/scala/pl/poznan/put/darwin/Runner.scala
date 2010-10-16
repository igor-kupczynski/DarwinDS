package pl.poznan.put.darwin.model

import java.io.FileOutputStream
import pl.poznan.put.darwin.simulation.{BriefReportGenerator, EvolutionReportGenerator,
                                        DMReportGenerator, Simulation, DMMock}
import pl.poznan.put.darwin.model.problem.{Parser, Problem}
import pl.poznan.put.darwin.model.solution._
import org.ini4j.ConfigParser
  
class Runner(problemFilename: String, configFilename: String) {
  
  def run() {

    val parser = new ConfigParser()
    parser.read(configFilename)
  
    val config = new Config(parser)

    val lines = io.Source.fromFile(problemFilename).mkString
    val problem: Problem = Parser.fromText(lines)
   
    val sim = new Simulation(config, problem)

    val evoOut = if (config.EVOLUTION_REPORT != "")
      new FileOutputStream(config.EVOLUTION_REPORT) else null
    val dmOut = if (config.DM_REPORT != "")
      new FileOutputStream(config.DM_REPORT) else null

    var markedSolutions: List[MarkedSolution] = null
    var evaluatedSolutions: List[EvaluatedSolution] = null
  
    try {
      if (config.BRIEF_REPORT)
        sim.registerObserver(new BriefReportGenerator(sim))
      if (evoOut != null) {
        sim.registerObserver(new EvolutionReportGenerator(sim, evoOut))
      }
      if (dmOut != null) {
        sim.registerObserver(new DMReportGenerator(sim, dmOut))
      }
      val dmMock = new DMMock(sim)
      var idx = 0
      while (idx < config.OUTER_COUNT) {
        evaluatedSolutions = sim.run(markedSolutions)
        markedSolutions = dmMock(evaluatedSolutions)
        idx = idx + 1
      }
    } finally {
      if (evoOut != null)
        evoOut.close()
      if (dmOut != null)
        dmOut.close()
    }
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
      getArg(args, "--problem", "etc/problems/dtlz7_c4.mod"),
      getArg(args, "--config", "etc/config.ini")
      )
    ).run()
  }
}
