package pl.put.poznan.darwin.model

import Config.{Scenario, Solution};

object Runner {
  def main(args: Array[String]) {
    val p = DefaultProblemFactory.getProblem();
    println(p);

    val scenario : Scenario = {
      case "pa"  =>  20; case "pb"  => 30; case "pc"  =>   25
      case "ta"  =>   5; case "tb"  =>  8; case "tc"  =>   10
      case "r1a" =>   1; case "r1b" =>  2; case "r1c" => 0.75; case "r1p" => 6
      case "r2a" => 0.5; case "r2b" =>  1; case "r2c" =>  0.5; case "r2p" => 9
      case "da"  =>  10; case "db"  => 20; case "dc"  =>   10
    }

    val solution : Solution = {
      case "xa" => 5; case "xb" => 10; case "xc" => 5
    }

    p.goals foreach ((g : Goal) => println(g.name + ": " + ExpressionEvaluator.evaluate(g.exp, scenario, solution)))
  }
}