package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.solution.{RankedSolution, MarkedSolution}
import pl.poznan.put.darwin.jrsintegration.{AbstractRulesContainer,
                                            JrsIntegration}
import com.weiglewilczek.slf4s.Logging

class DarwinEvolver extends Logging {

  def preformEvolution(solutions: List[MarkedSolution]): List[RankedSolution] = {
    val sim = solutions(0).sim
    var rulesContainer: AbstractRulesContainer = JrsIntegration(solutions)
    logger info "Got rules container"
    val engine = new EvolutionEngine(sim, rulesContainer)
    logger info "Evolution engine prepared"
    engine.start(solutions)
  }

}
