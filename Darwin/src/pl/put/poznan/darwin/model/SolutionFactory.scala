package pl.put.poznan.darwin.model

import Config.Solution
import scala.util.Random


abstract class SolutionFactory {
  def generate(p: Problem): Solution
}

object SimpleSolutionFactory extends SolutionFactory {

  private val rng: Random = Config.getRNG()

  def generate(p: Problem): Solution = {
    var result: Map[String, Double] = Map()
    p.getVariables() foreach ((v: Variable) => {
      result(v.name) = rng.nextDouble() * Config.MAX_VARIABLE;
    })
    result
  }
}