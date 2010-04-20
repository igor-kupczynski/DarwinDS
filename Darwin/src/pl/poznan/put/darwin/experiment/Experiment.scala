package pl.poznan.put.darwin.experiment

import pl.poznan.put.darwin.model.Config.Scenario
import pl.poznan.put.darwin.model.problem.Problem
import pl.poznan.put.darwin.model.Solution

object Experiment {
  def perform(problem: Problem,
              scenarios: List[Scenario],
              solutions: List[Solution]): List[Solution] = {
    var result: List[Solution] = Nil

    solutions foreach ((sol: Solution) => {
      val solution: Solution = new Solution(sol.problem, sol.values)
      scenarios foreach ((scen: Scenario) => solution.evaluateScenario(scen))
      result = solution :: result
    })
    result
  }

}