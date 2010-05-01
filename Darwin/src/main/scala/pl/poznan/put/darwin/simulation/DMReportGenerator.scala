package pl.poznan.put.darwin.simulation

import java.io.{OutputStream, PrintWriter}
import pl.poznan.put.darwin.model.solution.MarkedSolution
  
/**
 * Class reciving DM events and generating CSV report with progress
 *
 * @author: Igor Kupczynski
 */
class DMReportGenerator(sim: Simulation, stream: OutputStream) extends SimulationObserver {

  val writer = new PrintWriter(stream)
  var outerIdx = 0

  printTag()
  
  override def notify(event: SimulationEvent) {
    event match {
        case DMChoiceEvent(sols) => newDMChoices(sols)
        case NewGenerationEvent(sols) => {}
    }
  }

  private def newDMChoices(solutions: List[MarkedSolution]) {
    solutions.zipWithIndex foreach { case (s, idx) => {
      writer.print("%d,%d,%d" format (outerIdx, idx, if (s.good) 1 else 0 ))
      sim.problem.goals foreach { g =>
        sim.config.PERCENTILES foreach { p =>
          writer.print(",%f" format (s.getPercentile(g, p))) }
      }
      writer.println()
    }
    }
    writer.flush()
    outerIdx += 1
  }

  
  private def printTag() {
    writer.print("outer,idx,good")
    sim.problem.goals foreach { g =>
      sim.config.PERCENTILES foreach { p =>
        writer.print(",%s_%s" format (g.name, p)) }
    }
      writer.println()
      writer.flush()
  }
}
