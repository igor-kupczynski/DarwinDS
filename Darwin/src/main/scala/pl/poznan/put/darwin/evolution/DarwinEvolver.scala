package pl.poznan.put.darwin.evolution

import observer.{OneCriterionBestSolutionPrinter, GenerationObserver}
import pl.poznan.put.darwin.model.solution.{RankedSolution, MarkedSolution}
import pl.poznan.put.darwin.jrsintegration.{DarwinRulesContainer, JrsIntegration}

class DarwinEvolver {

  def preformEvolution(solutions: List[MarkedSolution]): List[RankedSolution] = {
    var rulesContainer: DarwinRulesContainer = JrsIntegration(solutions)
    val params: EvolutionParameters =
        new EvolutionParameters(solutions(0).sim.problem, rulesContainer)
    val engine = new EvolutionEngine(params)
    //engine.registerGenerationObserver(GenerationObserver())
    engine.registerGenerationObserver(new OneCriterionBestSolutionPrinter())
    engine.start(solutions)
  }

}
