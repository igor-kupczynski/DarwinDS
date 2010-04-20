package pl.poznan.put.darwin.evolution

import observer.{OneCriterionBestSolutionPrinter, GenerationObserver}
import pl.poznan.put.darwin.jrsintegration.{JrsIntegration, ScoreKeeper}
import pl.poznan.put.darwin.model.problem.Problem
import pl.poznan.put.darwin.model.Solution

class Evolver(problem: Problem) {
  def preformEvolution(baseResult: List[Solution]): List[Solution] = {
    println("Started evolution")
    var sk: ScoreKeeper = JrsIntegration.getScores(baseResult)
    val params: EvolutionParameters = new EvolutionParameters(problem, sk)
    val engine = new DarwinEvolutionEngine(params)
    //engine.registerGenerationObserver(GenerationObserver())
    engine.registerGenerationObserver(new OneCriterionBestSolutionPrinter())
    engine.start(baseResult)
  }
}