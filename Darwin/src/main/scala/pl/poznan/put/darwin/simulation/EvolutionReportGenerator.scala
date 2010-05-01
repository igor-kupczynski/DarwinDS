package pl.poznan.put.darwin.simulation

import java.io.{OutputStream, PrintWriter}
import pl.poznan.put.darwin.model.solution.RankedSolution
  
/**
 * Class reciving simulation events and generating CSV report with progress
 *
 * @author: Igor Kupczynski
 */
class EvolutionReportGenerator(sim: Simulation, stream: OutputStream) extends SimulationObserver {

  val writer = new PrintWriter(stream)
  var generation = 0
  var outerIdx = 0

  printTag()
  
  override def notify(event: SimulationEvent) {
    event match {
        case DMChoiceEvent(sols) => nextOuter()
        case NewGenerationEvent(sols) => newGeneration(sols)
    }
  }

  private def newGeneration(solutions: List[RankedSolution]) {
    solutions foreach { s => {
      writer.print("%d,%d,%d,%f,%G" format (outerIdx, generation, s.rank,
                                            s.primaryScore, s.secondaryScore))
      sim.problem.goals foreach { g =>
        sim.config.PERCENTILES foreach { p =>
          writer.print(",%f" format (s.getPercentile(g, p))) }
      }
      writer.println()
    }
    }
    writer.flush()
    generation += 1
  }

  private def nextOuter() {
    outerIdx += 1
    generation = 0
  }
  
  private def printTag() {
    writer.print("outer,generation,rank,primary,secondary")
    sim.problem.goals foreach { g =>
      sim.config.PERCENTILES foreach { p =>
        writer.print(",%s_%s" format (g.name, p)) }
    }
    writer.println()
    writer.flush()
  }
}
