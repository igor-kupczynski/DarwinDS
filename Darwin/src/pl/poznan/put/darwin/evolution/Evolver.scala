package pl.poznan.put.darwin.evolution

import collection.mutable.HashMap
import pl.poznan.put.darwin.experiment.SolutionResult
import pl.poznan.put.darwin.model.{Config, Problem}
import pl.poznan.put.darwin.model.Config.Solution
import pl.poznan.put.darwin.jrsintegration.{JrsIntegration, ScoreKeeper}

class Evolver(problem: Problem) {
  def preformEvolution(baseResult: HashMap[Solution, SolutionResult]): HashMap[Solution, SolutionResult] = {
    print("Started evolution")
    var sk: ScoreKeeper = JrsIntegration.getScores(baseResult)
    null
  }
}