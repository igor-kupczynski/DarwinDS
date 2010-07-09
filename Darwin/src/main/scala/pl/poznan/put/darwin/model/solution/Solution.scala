package pl.poznan.put.darwin.model.solution

import java.util.Random
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem._
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
    // Check problem constraints
    sim.problem.constraints foreach ((c: Constraint) => {
       val lVal = Evaluator.evaluate(c.lhs, default, values)
       val rVal = Evaluator.evaluate(c.rhs, default, values)
       if ( c.gte && lVal < rVal) {
         return false
       }
       if (!c.gte && lVal > rVal) {
         return false
       }
    })
    // Check additional variable constrints
    sim.problem.getVariables() foreach {
      case VariableDef(name, min, max, aConstraint) => {
        val v = values(name)
        if (v < min) return false
        if (v > max) return false
        aConstraint match {
            case BinaryConstraint  => { if (v != 0.0 && v != 1.0) return false }
            case IntegerConstraint => { if (v != math.round(v)) return false }
            case _ => {}
        }
      }
    }
    true
  }

  /**
   *  Generate neighbour
   */
  def randomNeighbour(): Solution = {
    var resultValues: Map[String, Double] = null
    val rng: RNG = RNG()
    var tries = 0
    while (tries < sim.config.MUTATION_TRIES &&
            (resultValues == null || !(new Solution(sim, resultValues)).isFeasible)) {
      val idx = rng.nextInt(values.size)
      val variable = (values.keys.toList)(idx)

      val v = values(variable)
      var factor = 1.0
  
      val value = sim.problem.getVariable(variable) match {
          case VariableDef(_, _, _, BinaryConstraint) =>
            { if (v == 0.0) 1.0 else 0.0}
          case VariableDef(_, min, max, IntegerConstraint) => {
            val d1 = v - min
            val d2 = max - v
            factor = (if (d1 < d2) d1 else d2) * 0.15
            math.round(boundaryAdd(v, min, max, rng.nextGaussian() * factor))
          }
          case VariableDef(_, min, max, null) =>
            val d1 = v - min
            val d2 = max - v
            factor = (if (d1 < d2) d1 else d2) * 0.15
            boundaryAdd(v, min, max, rng.nextGaussian() * factor)
      }
      resultValues = Map()
      values.keys foreach ((v: String) => {
        resultValues += (v -> (if (v == variable) value else values(v)))
      })
      tries += 1
    }
    Solution(sim, resultValues)
  }

  /**
   * Add two values with respect to boundaries of first value
   */
  private[solution] def boundaryAdd(v: Double, min: Double,
                                    max: Double, toAdd: Double): Double = {
    var current = v
    var delta = toAdd
    while (delta != 0.0) {
      current += delta
      delta = 0.0
      if (current > max) {
        delta = max - current
        current = max
      } else if (current < min) {
        delta = min - current
        current = min
      }
    }
    current
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
    val rng: RNG = RNG()
    var result: Map[String, Double] = null
    while (result == null || !(new Solution(sim, result)).isFeasible) {
      result = Map()
      sim.problem.getVariables foreach ((v: VariableDef) => {
        v.constraint match {
            case BinaryConstraint =>
              result += (v.name -> (if (rng.nextBoolean()) 1.0 else 0.0))
            case IntegerConstraint =>
              result += (v.name -> math.round(rng.nextDouble() * (v.max - v.min) + v.min))
            case _ =>
              result += (v.name -> (rng.nextDouble() * (v.max - v.min) + v.min))
        }
      })
    }
    new Solution(sim, result)
  }
}
