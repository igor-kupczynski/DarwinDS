package pl.poznan.put.darwin.model.problem

import  pl.poznan.put.darwin.model.Config.{Solution, Scenario}

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
  private def simplify(exp: Expr, scenario: Scenario, solution: Solution): Expr = {

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
      case BinaryOp("+", x, y) => evaluateSim(x) + evaluateSim(y)
      case BinaryOp("-", x, y) => evaluateSim(x) - evaluateSim(y)
      case BinaryOp("*", x, y) => evaluateSim(x) * evaluateSim(y)
      case BinaryOp("/", x, y) => evaluateSim(x) / evaluateSim(y)
      case AggregateOp("min", exps) => (exps.foldLeft[Double](Double.MaxValue))((x, exp) => Math.min(x,
                                                                                                     evaluateSim(exp)))
    }

  }


  /**
   * Simplify and evaluated expression
   */
  def evaluate(expr: Expr, scenario: Scenario, solution: Solution): Double = {
    evaluateSim(simplify(expr, scenario, solution))
  }
}