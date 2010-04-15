package pl.poznan.put.darwin.experiment

import pl.poznan.put.darwin.model.Config.{Scenario, Solution}
import collection.mutable.HashMap
import pl.poznan.put.darwin.model.problem.Problem

object Experiment {
  def perform(problem: Problem,
              scenarios: List[Scenario],
              solutions: List[Solution]): List[Tuple2[Solution, SolutionResult]] = {
    var result: List[Tuple2[Solution, SolutionResult]] = Nil

    solutions foreach ((sol: Solution) => {
      val solutionResult: SolutionResult = new SolutionResult(problem)
      scenarios foreach ((scen: Scenario) => solutionResult.addResult(problem.evaluate(scen, sol)))
      result = (sol, solutionResult) :: result
    })
    result
  }

}