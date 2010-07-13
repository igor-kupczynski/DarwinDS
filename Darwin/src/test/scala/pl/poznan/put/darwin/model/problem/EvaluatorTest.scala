package pl.poznan.put.darwin.model.problem
import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite

class EvaluatorTest extends JUnitSuite {

  @Test def EvaluatorTest = {

    val scenario: Map[String, Double] = Map("limit" -> 10.0)
    val solution: Map[String, Double] = Map("x" -> 5.0)

    def eval(e: Expr): Double = {
      Evaluator.evaluate(e, scenario, solution)
    }

    val limit = Interval("limit", 0, 20)
    val x = Variable("x")

    val e1 = UnaryOp("-", x)
    assertEquals(-5.0, eval(e1), 0.0)

    val e1b = UnaryOp("ln", x)
    assertEquals(1.60943791, eval(e1b), 0.000001)

    val e2 = BinaryOp("+", x, limit)
    assertEquals(15.0, eval(e2), 0.0)

    val e3 = BinaryOp("-", x, limit)
    assertEquals(-5.0, eval(e3), 0.0)

    val e4 = BinaryOp("*", x, limit)
    assertEquals(50.0, eval(e4), 0.0)

    val e5 = BinaryOp("/", x, limit)
    assertEquals(0.5, eval(e5), 0.0)

    val e6 = AggregateOp("min", limit :: x :: Constant(25) :: Nil)
    assertEquals(5.0, eval(e6), 0.0)

    val e6b = AggregateOp("sum", limit :: x :: Constant(25) :: Nil)
    assertEquals(40.0, eval(e6b), 0.0)
    
    val one = Constant(1)
    val zero = Constant(0)

    //  ( (15 + 0) * -(-1) ) / (+1) = 15
    val e7 = BinaryOp("/", BinaryOp("*", BinaryOp("+", Constant(15), zero),
      UnaryOp("-", UnaryOp("-", one))), UnaryOp("+", one))
    assertEquals(15.0, eval(e7), 0.0)
  }

}
