package pl.poznan.put.darwin.experiment

import pl.poznan.put.darwin.model.Config.{Scenario, Solution}
import collection.mutable.HashMap
import pl.poznan.put.darwin.model.{Goal, Problem}

object Experiment {

  def perform(problem: Problem,
              scenarios: List[Scenario],
              solutions: List[Solution]): HashMap[Solution, HashMap[Goal, List[Double]]] = {
    val result = new HashMap[Solution, HashMap[Goal, List[Double]]]

    solutions foreach ((sol: Solution) => {
      val solutionResult = new HashMap[Goal, List[Double]]
      problem.goals foreach {case g => solutionResult(g) = Nil}

      scenarios foreach {(scen: Scenario) =>
        problem.evaluate(scen, sol) foreach {case (g, res) => solutionResult(g) = res :: solutionResult(g)}
      }

      result(sol) = solutionResult
    })
    result
  }

}