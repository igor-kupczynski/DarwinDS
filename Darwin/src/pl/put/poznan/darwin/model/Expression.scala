package pl.put.poznan.darwin.model

import Config.Scenario
import Config.Solution

abstract class Expression {
  def *(other: Expression): Expression = {
    Product(this :: other :: Nil)
  }
}

case class Constant(value: Double) extends Expression
case class Variable(name: String) extends Expression
case class Interval(name: String, lower: Double, upper: Double) extends Expression
case class Sum(exps: List[Expression]) extends Expression
case class Minus(exp: Expression) extends Expression
case class Product(exps: List[Expression]) extends Expression
case class Min(exps: List[Expression]) extends Expression


case class Goal(name: String, exp: Expression, isMax: Boolean)


object ExpressionEvaluator {
  def evaluate(exp: Expression, scenario: Scenario, solution: Solution): Double = exp match {
    case Constant(x) => x
    case Variable(n) => solution(n)
    case Interval(n, lower, upper) => {
      val x = scenario(n)
      if (x < lower || x > upper) error("Value of scenario '" + x + "' => " + n + " outside interval")
      x
    }
    case Sum(exps) => (exps foldLeft 0.0){(x, y) => x + evaluate(y, scenario, solution)}
    case Minus(exp) => -evaluate(exp, scenario, solution)
    case Product(exps) => (exps foldLeft 1.0){(x, y) => x * evaluate(y, scenario, solution)}
    case Min(exps) => (exps foldLeft Double.MaxValue) {(x, y) => Math.min(x, evaluate(y, scenario, solution))}
  }
}