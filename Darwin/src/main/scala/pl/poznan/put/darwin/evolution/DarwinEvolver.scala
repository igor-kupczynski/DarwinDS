package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.solution.{RankedSolution, MarkedSolution}
import pl.poznan.put.darwin.jrsintegration.{DarwinRulesContainer, JrsIntegration}

class DarwinEvolver {

  def preformEvolution(solutions: List[MarkedSolution]): List[RankedSolution] = {
    val sim = solutions(0).sim
    var rulesContainer: DarwinRulesContainer = JrsIntegration(solutions)
    val engine = new EvolutionEngine(sim, rulesContainer)
    engine.start(solutions)
  }

}
