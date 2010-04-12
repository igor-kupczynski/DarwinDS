package pl.poznan.put.darwin.model.problem

import collection.mutable.HashMap
import pl.poznan.put.darwin.model.Config.{Scenario, Solution}
import pl.poznan.put.darwin.model.Config
import java.util.Random

/**
 *  Class representing MMO problem to be solved by the darwin method.
 *
 * @author: Igor Kupczynski
 */

class Problem(name: String, vars: List[VariableDef], goals: List[Goal], constraints: List[Constraint]) {

  private var intervals: List[Interval] = null

  /**
   * Returns scenario with medium values on each interval.
   */
  def getDefaultScenario(): Scenario = {
    val s = new HashMap[String, Double]()
    getIntervals().map((i: Interval) => {
      s(i.name) = i.getMediumValue()
    })
    s
  }

  /**
   * Checks if given solution is feasible (on default scenario)
   */
  def isFeasible(s: Solution): Boolean = {
    val default = getDefaultScenario()
    constraints foreach ((c: Constraint) => {
       val lVal = Evaluator.evaluate(c.lhs, default, s)
       val rVal = Evaluator.evaluate(c.rhs, default, s)
       if ( c.gte && lVal < rVal) return(false)
       if (!c.gte && lVal > rVal) return (false)
    })
    true
  }

  // TODO: Move! This should be part of solution or companion object
  def randomNeighbour(s: Solution) = {
    var result: HashMap[String, Double] = null
    val rng: Random = Config.getRNG()
    var tries = 0
    while (tries < Config.MUTATION_TRIES && (result == null || !isFeasible(result))) {
      val idx = rng.nextInt(vars.length)
      val variable = vars(idx)
      val value = s(variable.name) + rng.nextGaussian()
      result = new HashMap[String, Double]()
      vars foreach ((v: VariableDef) => {
        result(v.name) = if (v.name == variable.name) value else s(v.name)
      })
      tries += 1
    }
    result

  }

  /**
   * Returns all the intervals from problem
   */
  def getIntervals(): List[Interval] = {
    if (intervals == null) {
      intervals = Nil
      goals foreach ((g: Goal) => {
        Evaluator.extractIntervals(g.expr) foreach ((i: Interval) => {
          if (!intervals.contains(i)) intervals = i :: intervals
        })
      })
      constraints foreach ((c: Constraint) => {
        (Evaluator.extractIntervals(c.lhs) ::: Evaluator.extractIntervals(c.rhs)) foreach ((i: Interval) => {
          if (!intervals.contains(i)) intervals = i :: intervals
        })
      })
    }
    intervals
  }

  /**
   * Returns all the variables from problem
   */
  def getVariables(): List[VariableDef] = {
    vars
  }

  /**
   * Evaluates solution on given scenario
   */
  def evaluate(scenario: Scenario, solution: Solution): HashMap[Goal, Double] = {
    val result = new HashMap[Goal, Double]
    goals foreach ((g: Goal) => {
      result(g) = Evaluator.evaluate(g.expr, scenario, solution)
    })
    result
  }


  override def toString(): String = {
    val result = new StringBuilder()
    vars foreach ((v: VariableDef) => {
      result.append("%s;\n" format v)
    })
    result.append("\n")
    goals foreach ((g: Goal) => {
      result.append("%s;\n" format g)
    })
    result.append("\n")
    constraints foreach ((c: Constraint) => {
      result.append("%s;\n" format c)
    })
    return result.toString()
  }
}


/**
 * Companion object for Problem class, makes problem creation easier for parser.
 *
 * @author: Igor Kupczynski
 */
object Problem {
  def apply(elements: List[ProblemElement]): Problem = {
    var vars: List[VariableDef] = Nil
    var goals: List[Goal] = Nil
    var constraints: List[Constraint] = Nil

    def isDefined(v: Variable): Boolean = {
      vars foreach ((vDef: VariableDef) => if (vDef.name == v.name) return(true))
      return false
    }

    def checkVars(e: Expr) = {
      Evaluator.extractVariables(e) foreach ((v: Variable) =>
        if (!isDefined(v)) throw new Exception("Variable " + v.name + " is not defined")
      )
    }

    elements foreach (e => e match {
      case vd: VariableDef => {vars = vd :: vars}
      case Goal(name, expr, max) => {
        checkVars(expr)
        goals = Goal(name, expr, max) :: goals
      }
      case Constraint(name, lhs, rhs, gte) => {
        checkVars(lhs)
        checkVars(rhs)
        constraints = Constraint(name, lhs, rhs, gte) :: constraints 
      }
    })

    new Problem("----", vars.reverse, goals.reverse, constraints.reverse)
  }
}