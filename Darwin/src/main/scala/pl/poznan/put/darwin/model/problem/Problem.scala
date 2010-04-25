package pl.poznan.put.darwin.model.problem

/**
 *  Class representing MMO problem to be solved by the darwin method.
 *
 * @author: Igor Kupczynski
 */

class Problem(name: String, vars: List[VariableDef], val goals: List[Goal], val utilityFunction: UtilityFunction,
              val constraints: List[Constraint]) {

  private var intervals: List[Interval] = null

  /**
   * Returns scenario with medium values on each interval.
   */
  def getDefaultScenario(): Map[String, Double] = {
    var s: Map[String, Double] = Map()
    getIntervals().map((i: Interval) => {
      s += (i.name -> i.getMediumValue())
    })
    s
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

    if (utilityFunction != null) result.append("%s;\n\n" format utilityFunction)

    constraints foreach ((c: Constraint) => {
      result.append("%s;\n" format c)
    })
    return result.toString()
  }
}


  /**
   *  Companion object for Problem class, makes problem creation easier for parser.
   *
   * @author: Igor Kupczynski
   */
object Problem {
  def apply(elements: List[ProblemElement]): Problem = {
    var vars: List[VariableDef] = Nil
    var goals: List[Goal] = Nil
    var constraints: List[Constraint] = Nil
    var utilityFunction: UtilityFunction = null

    def isDefined(v: Variable): Boolean = {
      vars foreach ((vDef: VariableDef) => if (vDef.name == v.name) return(true))
      return false
    }

    def goalDefined(v: Variable): Boolean = {
      goals foreach ((g: Goal) => if (g.name == v.name) return(true))
      return false
    }

    def checkVars(e: Expr) = {
      Evaluator.extractVariables(e) foreach ((v: Variable) =>
        if (!isDefined(v)) throw new Exception("Variable " + v.name + " is not defined")
      )
    }

    def checkGoals(e: Expr) = {
      Evaluator.extractVariables(e) foreach ((v: Variable) =>
        if (!goalDefined(v)) throw new Exception("!dec: Goal " + v.name + " is not defined")
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
      case UtilityFunction(expr) => {
        if (utilityFunction != null) throw new Exception("Utility function redefinition")
        checkGoals(expr)
        utilityFunction = UtilityFunction(expr)
      }
    })

    new Problem("----", vars.reverse, goals.reverse, utilityFunction, constraints.reverse)
  }
}