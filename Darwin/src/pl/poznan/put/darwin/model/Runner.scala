package pl.poznan.put.darwin.model

import problem.Parser
import scala.Iterator.range
import pl.poznan.put.darwin.experiment.{AutoEvaluator, Experiment}
import pl.poznan.put.darwin.evolution.Evolver

object Runner {
  def main(args: Array[String]) {
    
    val lines = io.Source.fromFile(Config.FILENAME).mkString
    val p = Parser.fromText(lines)

    var solutions: List[Solution] = Nil
    for (idx <- range(0, Config.SOLUTION_COUNT)) {
      solutions = SimpleSolutionFactory.generate(p) :: solutions
    }

    var scenarios: List[Map[String, Double]] = Nil
    for (idx <- range(0, Config.SCENARIO_COUNT)) {
      scenarios = MonteCarloScenarioFactory.generate(p) :: scenarios
    }

    var result = Experiment.perform(p, scenarios, solutions)
    println(result)

    val evolver = new Evolver(p)
    var idx = 0
    while (idx < 30) {
      println("loop " + idx)
      idx += 1
      val evaluatedResult = AutoEvaluator.evaluate(result)
      result = evolver.preformEvolution(evaluatedResult)
    }
  }
}

