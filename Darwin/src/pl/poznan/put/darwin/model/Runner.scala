package pl.poznan.put.darwin.model

import Config.{Scenario, Solution}
import scala.Iterator.range
import pl.poznan.put.darwin.experiment.{AutoEvaluator, SolutionResult, Experiment}
import pl.poznan.put.darwin.utils.SysOutPresenter
import pl.poznan.put.darwin.evolution.Evolver

object Runner {
  def main(args: Array[String]) {
    val p = DefaultProblemFactory.getProblem();
    println(p);

    val scenario: Scenario = {
      case "pa" => 20;
      case "pb" => 30;
      case "pc" => 25
      case "ta" => 5;
      case "tb" => 8;
      case "tc" => 10
      case "r1a" => 1;
      case "r1b" => 2;
      case "r1c" => 0.75;
      case "r1p" => 6
      case "r2a" => 0.5;
      case "r2b" => 1;
      case "r2c" => 0.5;
      case "r2p" => 9
      case "da" => 10;
      case "db" => 20;
      case "dc" => 10
    }

    val solution: Solution = {
      case "xa" => 5; case "xb" => 10; case "xc" => 5
    }

    p.goals foreach ((g: Goal) => println(g.name + ": " + ExpressionEvaluator.evaluate(g.exp, scenario, solution)))

    var solutions: List[Solution] = Nil
    for (idx <- range(0, Config.SOLUTION_COUNT)) {
      solutions = SimpleSolutionFactory.generate(p) :: solutions
    }
    println(solutions.head)

    var scenarios: List[Scenario] = Nil
    for (idx <- range(0, Config.SCENARIO_COUNT)) {
      scenarios = MonteCarloScenarioFactory.generate(p) :: scenarios
    }
    println(scenarios.head)

    var result = Experiment.perform(p, scenarios, solutions)
    println(result)

    val evolver = new Evolver(p)
    var idx = 0
    while (idx < 3) {
      println("loop " + idx)
      idx += 1
      val evaluatedResult = AutoEvaluator.evaluate(result)
      //SysOutPresenter showOutput evaluatedResult
      result = evolver.preformEvolution(evaluatedResult)
    }
  }
}

