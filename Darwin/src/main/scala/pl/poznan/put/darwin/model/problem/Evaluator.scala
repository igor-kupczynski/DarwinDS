package pl.poznan.put.darwin.model.problem

/**
 * Object for manipulation expressions:
 *   * Evaluating
 *   * Extracting intervals/variables
 *
 * @author: Igor Kupczynski
 */
object Evaluator {

  /**
   * Simplify mathematical expression and convert variables/intervals to constants
   */
  private def simplify(exp: Expr, scenario: Map[String, Double], solution: Map[String, Double]): Expr = {

    // first simplify sub expressions
    val simpSub: Expr = exp match {
      case AggregateOp(op, args) => AggregateOp(op, args map (x => simplify(x, scenario, solution)))
      case BinaryOp(op, l, r) => BinaryOp(op, simplify(l, scenario, solution), simplify(r, scenario, solution))
      case UnaryOp(op, x) => UnaryOp(op, simplify(x, scenario, solution))
      case Interval(x, _, _) => Constant(scenario(x))
      case Variable(x) => Constant(solution(x))
      case _ => exp
    }

    // and then Top
    def simplifyTop(exp: Expr): Expr = {
      exp match {
        case UnaryOp("-", UnaryOp("-", x)) => x
        case UnaryOp("+", x) => x

        case BinaryOp("*", x, Constant(1)) => x
        case BinaryOp("*", Constant(1), x) => x

        case BinaryOp("*", x, Constant(0)) => Constant(0)
        case BinaryOp("*", Constant(0), x) => Constant(0)

        case BinaryOp("/", x, Constant(1)) => x
        case BinaryOp("/", x1, x2) if x1 == x2 => Constant(1)

        case BinaryOp("+", x, Constant(0)) => x
        case BinaryOp("+", Constant(0), x) => x

        // Anything else cannot (yet) be simplified
        case e => e
      }
    }

    simplifyTop(simpSub)
  }


  /**
   * Evaluate previously simplified expression
   */
  private def evaluateSim(exp: Expr): Double = {

    exp match {

      case Constant(x) => x
      case UnaryOp("-", x) => -(evaluateSim(x))
      case UnaryOp("ln", x) => math.log(evaluateSim(x))
      case BinaryOp("+", x, y) => evaluateSim(x) + evaluateSim(y)
      case BinaryOp("-", x, y) => evaluateSim(x) - evaluateSim(y)
      case BinaryOp("*", x, y) => evaluateSim(x) * evaluateSim(y)
      case BinaryOp("/", x, y) => evaluateSim(x) / evaluateSim(y)
      case AggregateOp("min", exps) =>
        (exps.foldLeft[Double](Double.MaxValue))(
          (x, exp) => math.min(x, evaluateSim(exp))
        )
      case AggregateOp("sum", exps) =>
        (exps.foldLeft[Double](0))(
          (x, exp) => x + evaluateSim(exp)
        )
    }

  }


  /**
   * Simplify and evaluated expression
   */
  def evaluate(expr: Expr, scenario: Map[String, Double], solution: Map[String, Double]): Double = {
    evaluateSim(simplify(expr, scenario, solution))
  }


  /**
   * Extracts all intervals from the expression
   */
  def extractIntervals(exp: Expr): List[Interval] = {

    def extractAll(exp: Expr): List[Interval] = {
      exp match {
        case i: Interval => i :: Nil
        case UnaryOp(name, e) => extractAll(e)
        case BinaryOp(name, l, r) => extractAll(l) ::: extractAll(r)
        case AggregateOp(name, eList) =>  (eList.foldLeft[List[Interval]](Nil))((ll, e) => extractAll(e) ::: ll)
        case _ => Nil
      }
    }

    ((extractAll(exp)).foldLeft[List[Interval]](Nil))((ll: List[Interval], i) => if (ll.contains(i)) ll else i :: ll)
  }

  /**
   * Extracts all variables from the expression
   */
  def extractVariables(exp: Expr): List[Variable] = {

    def extractAll(exp: Expr): List[Variable] = {
      exp match {
        case v: Variable => v :: Nil
        case UnaryOp(name, e) => extractAll(e)
        case BinaryOp(name, l, r) => extractAll(l) ::: extractAll(r)
        case AggregateOp(name, eList) =>  (eList.foldLeft[List[Variable]](Nil))((ll, e) => extractAll(e) ::: ll)
        case _ => Nil
      }
  }

  ((extractAll(exp)).foldLeft[List[Variable]](Nil))((ll: List[Variable], i) => if (ll.contains(i)) ll else i :: ll)
  }
}
