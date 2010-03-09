package pl.poznan.put.darwin.experiment

import pl.poznan.put.darwin.model.Config.{Scenario, Solution}
import collection.mutable.HashMap
import pl.poznan.put.darwin.model.{Goal, Problem}

object Experiment {
  def perform(problem: Problem,
              scenarios: List[Scenario],
              solutions: List[Solution]): HashMap[Solution, SolutionResult] = {
    val result = new HashMap[Solution, SolutionResult]

    solutions foreach ((sol: Solution) => {
      val solutionResult: SolutionResult = new SolutionResult
      scenarios foreach ((scen: Scenario) => solutionResult.addResult(problem.evaluate(scen, sol)))
      result(sol) = solutionResult
    })
    result
  }

}