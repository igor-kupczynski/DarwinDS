abstract class Expr {
  def *(other: Expr): Expr = {
    Product(this :: other :: Nil)
  }
}

case class Constant(value: Double) extends Expr
case class Variable(name: String) extends Expr
case class Interval(name: String, lower: Double, upper: Double) extends Expr
case class Sum(exprs: List[Expr]) extends Expr
case class Minus(expr: Expr) extends Expr
case class Product(exprs: List[Expr]) extends Expr
case class Min(exprs: List[Expr]) extends Expr


case class Goal(name: String, expr: Expr, isMax: Boolean)