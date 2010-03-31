package pl.poznan.put.darwin.model.problem

/**
 * Module containing abstract data types for problem.
 *
 * @author: Igor Kupczynski
 */


abstract class Expr

/* Leafs */
case class Constant(value: Double) extends Expr
case class Variable(name: String, min: Double, max: Double) extends Expr
case class Interval(name: String, lower: Double, upper: Double) extends Expr {
  def getMiddleValue(): Double = {
    lower + ((upper - lower) / 2)
  }
}

/* Operators */
case class UnaryOp(operator: String, arg: Expr) extends Expr
case class BinaryOp(operator: String, lhs: Expr, rhs: Expr) extends Expr
case class AggregateOp(operator: String, args: List[Expr]) extends Expr

/* Goal: name, expr, max or min */
case class Goal(name: String, expr: Expr, max: Boolean)

/* Constraint: name, left expr, right expr, >= or <= */
case class Constraint(name: String, lhs: Expr, rhs: Expr, gte: Boolean)



