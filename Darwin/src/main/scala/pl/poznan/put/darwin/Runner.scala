package pl.poznan.put.darwin.model

import java.io.FileOutputStream
import pl.poznan.put.darwin.simulation.{BriefReportGenerator, EvolutionReportGenerator,
                                        DMReportGenerator, Simulation}
import pl.poznan.put.darwin.model.problem.{Parser, Problem}
import org.ini4j.ConfigParser
  
class Runner(problemFilename: String,
             configFilename: String,
             evoReportFilename: String,
             dmReportFilename: String) {
  
  def run() {

    val parser = new ConfigParser()
    parser.read(configFilename)
  
    val config = new Config(parser)

    val lines = io.Source.fromPath(problemFilename).mkString
    val problem: Problem = Parser.fromText(lines)
   
    val sim = new Simulation(config, problem)

    val evoOut = new FileOutputStream(evoReportFilename)
    val dmOut = new FileOutputStream(dmReportFilename)

    
    try {
      sim.registerObserver(new BriefReportGenerator(sim))
      sim.registerObserver(new EvolutionReportGenerator(sim, evoOut))
      sim.registerObserver(new DMReportGenerator(sim, dmOut))
      sim.run()
    } finally {
      evoOut.close()
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
      getArg(args, "--problem", "etc/problems/simple_1crit.mod"),
      getArg(args, "--config", "etc/config.ini"),
      getArg(args, "--evolution-report", "out/evolution_report.csv"),
      getArg(args, "--dm-report", "out/dm_report.csv")
      )
    ).run()
  }
}
