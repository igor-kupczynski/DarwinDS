package pl.poznan.put.darwin.model

import Config.Solution
import java.util.Random
import collection.mutable.HashMap


abstract class SolutionFactory {
  def generate(p: Problem): Solution
}

object SimpleSolutionFactory extends SolutionFactory {
  private val rng: Random = Config.getRNG()

  def generate(p: Problem): Solution = {
    val result = new HashMap[String, Double]
    p.getVariables() foreach ((v: Variable) => {
      result(v.name) = rng.nextDouble() * (v.max - v.min) + v.min;
    })
    result
  }
}