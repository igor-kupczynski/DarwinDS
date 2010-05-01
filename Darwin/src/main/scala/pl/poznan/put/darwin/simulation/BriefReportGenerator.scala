package pl.poznan.put.darwin.simulation

import java.io.{OutputStream, PrintWriter}
import pl.poznan.put.darwin.model.solution.{RankedSolution, MarkedSolution}
  
/**
 * Class reciving all events and generating simple report to stdout
 *
 * @author: Igor Kupczynski
 */
class BriefReportGenerator(sim: Simulation) extends SimulationObserver {

  var outerIdx = 0
  var generation = 0
  
  override def notify(event: SimulationEvent) {
    event match {
        case DMChoiceEvent(sols) => newDMChoices(sols)
        case NewGenerationEvent(sols) => newGeneration(sols)
    }
  }

  private def newDMChoices(solutions: List[MarkedSolution]) {
    println("--- DM Mock selected solutions---")
    outerIdx += 1
    generation = 0
  }

  private def newGeneration(solutions: List[RankedSolution]) {
    var bestPS = Double.MinValue
    var bestUtil = Double.MinValue
    solutions foreach { s => {
      if (s.primaryScore > bestPS) bestPS = s.primaryScore
      if (s.utilityFunctionValue > bestUtil) bestUtil = s.utilityFunctionValue
    }}
    println("> Gen [%02d] best PS=%10f, best utility=%10f" format (
        generation, bestPS,  bestUtil))
    generation += 1
  }

}
