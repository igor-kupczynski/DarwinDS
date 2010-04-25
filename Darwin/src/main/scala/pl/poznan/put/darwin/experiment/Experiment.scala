package pl.poznan.put.darwin.experiment

import pl.poznan.put.darwin.model.problem.Problem
import pl.poznan.put.darwin.model.solution.{EvaluatedSolution, Solution}

object Experiment {
  def apply(problem: Problem,
              scenarios: List[Map[String, Double]],
              solutions: List[Solution]): List[EvaluatedSolution] =
    solutions.map(s => EvaluatedSolution(s, scenarios))
}