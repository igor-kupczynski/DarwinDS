package pl.poznan.put.darwin.model.problem

/**
 * Module containing abstract syntax tree elements for problem.
 *
 * @author: Igor Kupczynski
 */
abstract class Expr

/* Leafs */
case class Constant(value: Double) extends Expr
case class Variable(name: String) extends Expr
case class Interval(name: String, lower: Double, upper: Double) extends Expr {
  def getMediumValue(): Double = {
    lower + ((upper - lower) / 2)
  }
}

/* Operators */
case class UnaryOp(operator: String, arg: Expr) extends Expr
case class BinaryOp(operator: String, lhs: Expr, rhs: Expr) extends Expr
case class AggregateOp(operator: String, args: List[Expr]) extends Expr

/**
 * Abstract base class for all problem elements
 */
abstract class ProblemElement

/* Goal: name, expr, max or min */
case class Goal(name: String, expr: Expr, max: Boolean) extends ProblemElement

/* Constraint: name, left expr, right expr, >= or <= */
case class Constraint(name: String, lhs: Expr, rhs: Expr, gte: Boolean) extends ProblemElement

/* Variable definition: name, min and max value */
case class VariableDef(name: String, min: Double, max: Double) extends ProblemElement



