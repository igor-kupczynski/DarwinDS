package pl.poznan.put.darwin.model

import problem.Parser
import scala.Iterator.range
import pl.poznan.put.darwin.experiment.DMMock
import pl.poznan.put.darwin.evolution.DarwinEvolver
import solution.{MarkedSolution, EvaluatedSolution, Solution}

object Runner {
  def main(args: Array[String]) {

    println("foo")
  
    val lines = io.Source.fromPath(Config.FILENAME).mkString
    val p = Parser.fromText(lines)

    var solutions: List[Solution] = Nil
    for (idx <- range(0, Config.SOLUTION_COUNT)) {
      solutions = Solution.random(p) :: solutions
    }

    var scenarios: List[Map[String, Double]] = Nil
    for (idx <- range(0, Config.SCENARIO_COUNT)) {
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

