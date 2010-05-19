package pl.poznan.put.darwin.model

import java.io.FileOutputStream
import pl.poznan.put.darwin.simulation.{BriefReportGenerator, EvolutionReportGenerator,
                                        DMReportGenerator, Simulation}
import pl.poznan.put.darwin.model.problem.{Parser, Problem}
import org.ini4j.ConfigParser
  
class Runner(problemFilename: String, configFilename: String) {
  
  def run() {

    val parser = new ConfigParser()
    parser.read(configFilename)
  
    val config = new Config(parser)

    val lines = io.Source.fromPath(problemFilename).mkString
    val problem: Problem = Parser.fromText(lines)
   
    val sim = new Simulation(config, problem)

    val evoOut = if (config.EVOLUTION_REPORT != "")
      new FileOutputStream(config.EVOLUTION_REPORT) else null
    val dmOut = if (config.DM_REPORT != "")
      new FileOutputStream(config.DM_REPORT) else null

    
    try {
      if (config.BRIEF_REPORT)
        sim.registerObserver(new BriefReportGenerator(sim))
      if (evoOut != null)
        sim.registerObserver(new EvolutionReportGenerator(sim, evoOut))
      if (dmOut != null)
        sim.registerObserver(new DMReportGenerator(sim, dmOut))
      sim.run()
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
      getArg(args, "--problem", "etc/problems/picking100.mod"),
      getArg(args, "--config", "etc/config.ini")
      )
    ).run()
  }
}
