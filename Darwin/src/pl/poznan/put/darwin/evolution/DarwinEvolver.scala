package pl.poznan.put.darwin.evolution

import observer.{OneCriterionBestSolutionPrinter, GenerationObserver}
import pl.poznan.put.darwin.jrsintegration.JrsIntegration
import pl.poznan.put.darwin.model.solution.{RankedSolution, MarkedSolution}
import pl.poznan.put.cs.idss.jrs.rules.RulesContainer

class DarwinEvolver {

  def preformEvolution(solutions: List[MarkedSolution]): List[RankedSolution] = {
    println("Started evolution")
    var rulesContainer: RulesContainer = JrsIntegration(solutions)
    val params: EvolutionParameters = new EvolutionParameters(solutions(0).problem, rulesContainer)
    val engine = new EvolutionEngine(params)
    //engine.registerGenerationObserver(GenerationObserver())
    engine.registerGenerationObserver(new OneCriterionBestSolutionPrinter())
    engine.start(solutions)
  }

}