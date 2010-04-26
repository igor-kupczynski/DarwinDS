package pl.poznan.put.darwin.simulation

import pl.poznan.put.darwin.evolution.DarwinEvolver
import pl.poznan.put.darwin.model.problem.Parser
import pl.poznan.put.darwin.model.solution._
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.MonteCarloScenarioFactory
  
class Simulation {

  val fired = false

  def run() {
    if (fired)
      throw new Exception("Already fired")

    val lines = io.Source.fromPath(Config.FILENAME).mkString
    val p = Parser.fromText(lines)

    var solutions: List[Solution] = Nil
    for (idx <- 1 to Config.SOLUTION_COUNT) {
      solutions = Solution.random(p) :: solutions
    }

    var scenarios: List[Map[String, Double]] = Nil
    for (idx <- 1 to Config.SCENARIO_COUNT) {
      scenarios = MonteCarloScenarioFactory.generate(p) :: scenarios
    }

    var evaluatedSolutions = EvaluatedSolution(solutions, scenarios)

    val evolver = new DarwinEvolver()
    var idx = 0
    while (idx < 10) {
      println("loop " + idx)
      idx += 1
      val markedResult: List[MarkedSolution] = DMMock(evaluatedSolutions)
      evaluatedSolutions = evolver.preformEvolution(markedResult)
    }
  }
}
