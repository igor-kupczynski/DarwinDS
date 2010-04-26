package pl.poznan.put.darwin.model.solution

import java.util.Random
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.{Evaluator, Constraint, VariableDef, Problem}

/**
 * Base class for solution hierarchy.
 *
 * Remembers values of variables
 */
class Solution(val problem: Problem, val values: Map[String, Double]) {

  def goals = problem.goals


    /**
   * Checks if given solution is feasible (on default scenario)
   */
  def isFeasible: Boolean = {
    val default = problem.getDefaultScenario()
    problem.constraints foreach ((c: Constraint) => {
       val lVal = Evaluator.evaluate(c.lhs, default, values)
       val rVal = Evaluator.evaluate(c.rhs, default, values)
       if ( c.gte && lVal < rVal) return(false)
       if (!c.gte && lVal > rVal) return (false)
    })
    true
  }

  /**
   *  Generate neighbour
   */
  def randomNeighbour(): Solution = {
    var resultValues: Map[String, Double] = null
    val rng: Random = Config.getRNG()
    var tries = 0
    while (tries < Config.MUTATION_TRIES &&
            (resultValues == null || !(new Solution(problem, resultValues)).isFeasible)) {
      val idx = rng.nextInt(values.size)
      val variable = (values.keys.toList)(idx)
      val value = values(variable) + rng.nextGaussian()
      resultValues = Map()
      values.keys foreach ((v: String) => {
        resultValues += (v -> (if (v == variable) value else values(v)))
      })
      tries += 1
    }
    Solution(problem, resultValues)
  }

  val name = "Solution"

  override def equals(that: Any) = that match {
    case other: Solution => other.getClass == getClass &&
      other.problem == problem && other.values == values
    case _ => false
  }
  
  /**
  * Returns string representation of solution
  */ 
  override def toString(): String = {
    name + ": " + stringValues
  }

  protected def stringValues(): String = {
    values.toString
  }

}


/**
 *  Companion object for creating solutions
 */
object Solution {

  def apply(p: Problem, values: Map[String, Double]): Solution = {
    new Solution(p, values)
  }

  def random(p: Problem): Solution = {
    val rng: Random = Config.getRNG()
    var result: Map[String, Double] = null
    while (result == null || !(new Solution(p, result)).isFeasible) {
      result = Map()
      p.getVariables foreach ((v: VariableDef) => {
        result += (v.name -> (rng.nextDouble() * (v.max - v.min) + v.min))
      })
    }
    new Solution(p, result)
  }
}
