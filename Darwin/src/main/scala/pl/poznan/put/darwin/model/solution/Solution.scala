package pl.poznan.put.darwin.model.solution

import java.util.Random
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.{Evaluator, Constraint, VariableDef, Problem}
import pl.poznan.put.darwin.simulation.Simulation
import pl.poznan.put.darwin.utils.RNG

/**
 * Base class for solution hierarchy.
 *
 * Remembers values of variables
 */
class Solution(val sim: Simulation, val values: Map[String, Double]) {

  def goals = sim.problem.goals


    /**
   * Checks if given solution is feasible (on default scenario)
   */
  def isFeasible: Boolean = {
    val default = sim.problem.getDefaultScenario()
    sim.problem.constraints foreach ((c: Constraint) => {
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
    val rng: Random = RNG.get()
    var tries = 0
    while (tries < sim.config.MUTATION_TRIES &&
            (resultValues == null || !(new Solution(sim, resultValues)).isFeasible)) {
      val idx = rng.nextInt(values.size)
      val variable = (values.keys.toList)(idx)
      val value = values(variable) + rng.nextGaussian()
      resultValues = Map()
      values.keys foreach ((v: String) => {
        resultValues += (v -> (if (v == variable) value else values(v)))
      })
      tries += 1
    }
    Solution(sim, resultValues)
  }

  val name = "Solution"

  override def equals(that: Any) = that match {
    case other: Solution => other.getClass == getClass &&
      other.sim == sim && other.values == values
    case _ => false
  }

  override def hashCode: Int = sim.problem.hashCode + 41 * values.hashCode
  
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

  def apply(sim: Simulation, values: Map[String, Double]): Solution = {
    new Solution(sim, values)
  }

  def random(sim: Simulation): Solution = {
    val rng: Random = RNG.get()
    var result: Map[String, Double] = null
    while (result == null || !(new Solution(sim, result)).isFeasible) {
      result = Map()
      sim.problem.getVariables foreach ((v: VariableDef) => {
        result += (v.name -> (rng.nextDouble() * (v.max - v.min) + v.min))
      })
    }
    new Solution(sim, result)
  }
}
