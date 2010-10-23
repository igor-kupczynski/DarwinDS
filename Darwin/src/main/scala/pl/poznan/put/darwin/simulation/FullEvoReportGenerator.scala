package pl.poznan.put.darwin.simulation

import java.io.{OutputStream, PrintWriter}
import pl.poznan.put.darwin.model.solution.RankedSolution
import pl.poznan.put.darwin.model.problem.Evaluator

/**
 * Class reciving simulation events and generating CSV report with progress
 *
 * @author: Igor Kupczynski
 */
class FullEvoReportGenerator(sim: Simulation, stream: OutputStream) extends SimulationObserver {

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
    solutions foreach {
      s => {
        var scenarioIdx = 0
        for (scen <- sim.scenarios) {
          writer.print("%d,%d,%d,%d,%f,%G" format (outerIdx, generation, s.rank,
                  scenarioIdx, s.primaryScore,
                  s.secondaryScore))
          sim.problem.goals foreach {
            g =>
              writer.print(",%f" format Evaluator.evaluate(g.expr, scen, s.values))
          }
          scenarioIdx += 1
          writer.println()
        }
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
    writer.print("outer,generation,rank,scenario,primary,secondary")
    sim.problem.goals foreach { g =>
      writer.print(",%s" format g.name)
    }
    writer.println()
    writer.flush()
  }
}
