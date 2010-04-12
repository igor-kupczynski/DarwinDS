package pl.poznan.put.darwin.model.problem

/**
 * Module containing abstract syntax tree elements for problem.
 *
 * @author: Igor Kupczynski
 */
abstract class Expr

/* Leafs */
case class Constant(value: Double) extends Expr {
  override def toString: String = {
    value.toString
  }
}
case class Variable(name: String) extends Expr {
  override def toString: String = {
    name
  }
}
case class Interval(name: String, lower: Double, upper: Double) extends Expr {
  def getMediumValue(): Double = {
    lower + ((upper - lower) / 2)
  }

  override def toString: String = {
    "[%s: %s, %s]" format (name, lower, upper)
  }
}

/* Operators */
case class UnaryOp(operator: String, arg: Expr) extends Expr {
  override def toString: String = {
    "%s(%s)" format (operator, arg)
  }
}
case class BinaryOp(operator: String, lhs: Expr, rhs: Expr) extends Expr {
  override def toString: String = {
    "(%s %s %s)" format (lhs, operator, rhs)
  }
}
case class AggregateOp(operator: String, args: List[Expr]) extends Expr {
  override def toString: String = {
    val result: StringBuilder = new StringBuilder()
    result.append(operator)
    result.append("(")
    result.append(args.foldLeft[String]("")((a: String, b: Expr) => { if (a == "") b.toString else "%s, %s" format (a, b)}))
    result.append(")")
    result.toString()
  }
}

/**
 * Abstract base class for all problem elements
 */
abstract class ProblemElement

/* Goal: name, expr, max or min */
case class Goal(name: String, expr: Expr, max: Boolean) extends ProblemElement {
   override def toString: String = {
      "%s %s: %s" format (if (max) "max" else "min" , name, expr)
   }
}

/* Constraint: name, left expr, right expr, >= or <= */
case class Constraint(name: String, lhs: Expr, rhs: Expr, gte: Boolean) extends ProblemElement {
   override def toString: String = {
      "%s: %s %s %s" format (name, lhs, if (gte) ">=" else "<=" , rhs)
   }
}

/* Variable definition: name, min and max value */
case class VariableDef(name: String, min: Double, max: Double) extends ProblemElement {
  override def toString: String = {
    "var[%s, %s] %s" format (min, max, name)
  }
}



