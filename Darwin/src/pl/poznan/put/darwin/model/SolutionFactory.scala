package pl.poznan.put.darwin.model

import java.util.Random
import collection.immutable.HashMap
import problem.{VariableDef, Problem}

abstract class SolutionFactory {
  def generate(p: Problem): Solution
}

object SimpleSolutionFactory extends SolutionFactory {
  private val rng: Random = Config.getRNG()

  def generate(p: Problem): Solution = {
    var result: Map[String, Double] = null
    while (result == null || !p.isFeasible(result)) {
      result = new HashMap[String, Double]
      p.getVariables foreach ((v: VariableDef) => {
        result += (v.name -> (rng.nextDouble() * (v.max - v.min) + v.min))
      })
    }
    new Solution(p, result)
  }
}