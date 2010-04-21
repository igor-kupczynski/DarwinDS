package pl.poznan.put.darwin.model.solution

import collection.immutable.HashMap
import pl.poznan.put.darwin.model.problem.{VariableDef, Problem}
import pl.poznan.put.darwin.model.Config
import java.util.Random


/**
 * Base class for solution hierarchy.
 *
 * Remembers values of variables
 */
class Solution(val problem: Problem, val values: Map[String, Double]) {

  def goals = problem.goals

}


/**
 * Companion object for creating solutions
 */
object Solution {

  def apply(p: Problem, values: Map[String, Double]): Solution = {
    new Solution(p, values)
  }

  def random(p: Problem): Solution = {
    val rng: Random = Config.getRNG()
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
