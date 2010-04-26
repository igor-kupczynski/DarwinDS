package pl.poznan.put.darwin.simulation

import pl.poznan.put.darwin.evolution.DarwinEvolver
import pl.poznan.put.darwin.model.problem.Problem
import pl.poznan.put.darwin.model.solution._
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.Scenario
  
class Simulation(val config: Config, val problem: Problem) {
  
  val fired = false

  def run() {
    if (fired)
      throw new Exception("Already fired")
    
    var solutions: List[Solution] = Nil
    for (idx <- 1 to config.SOLUTION_COUNT) {
      solutions = Solution.random(this) :: solutions
    }

    var scenarios: List[Map[String, Double]] = Nil
    for (idx <- 1 to config.SCENARIO_COUNT) {
      scenarios = Scenario.generate(problem) :: scenarios
    }

    var evaluatedSolutions = EvaluatedSolution(solutions, scenarios)

    val evolver = new DarwinEvolver()
    var idx = 0

    val dMMock = new DMMock(this)
    
    while (idx < 10) {
      println("loop " + idx)
      idx += 1
      val markedResult: List[MarkedSolution] = dMMock(evaluatedSolutions)
      evaluatedSolutions = evolver.preformEvolution(markedResult)
    }
  }
}
