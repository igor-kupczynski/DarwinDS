package pl.poznan.put.darwin.evolution

import collection.mutable.HashMap
import observer.GenerationObserver
import pl.poznan.put.darwin.experiment.SolutionResult
import pl.poznan.put.darwin.model.{Config, Problem}
import pl.poznan.put.darwin.model.Config.Solution
import pl.poznan.put.darwin.jrsintegration.{JrsIntegration, ScoreKeeper}

class Evolver(problem: Problem) {
  def preformEvolution(baseResult: List[Tuple2[Solution, SolutionResult]]): List[Tuple2[Solution, SolutionResult]] = {
    println("Started evolution")
    var sk: ScoreKeeper = JrsIntegration.getScores(baseResult)
    val params: EvolutionParameters = new EvolutionParameters(problem, sk)
    val engine = new DarwinEvolutionEngine(params)
    engine.registerGenerationObserver(GenerationObserver())
    engine.start(baseResult)
  }
}