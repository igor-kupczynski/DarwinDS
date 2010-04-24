package pl.poznan.put.darwin.model.problem
import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite

class ProblemADTTest extends JUnitSuite {

  @Test def LeafTest = {
    val c1 = Constant(-15)
    assertEquals(-15.0, c1.value, 0.0)
    assertEquals("-15.0", c1.toString)

    val v1 = Variable("x1")
    assertEquals("x1", v1.name)
    assertEquals("x1", v1.toString)

    val i1 = Interval("limit1", -1, 10)
    assertEquals("limit1", i1.name)
    assertEquals(-1.0, i1.lower, 0.0)
    assertEquals(10.0, i1.upper, 0.0)
    assertEquals(4.5, i1.getMediumValue(), 0.0)
    assertEquals("[limit1: -1.0, 10.0]", i1.toString)

    val i2 = Interval("limit2", 3.14, 3.14)
    assertEquals("limit2", i2.name)
    assertEquals(3.14, i2.lower, 0.0)
    assertEquals(3.14, i2.upper, 0.0)
    assertEquals(3.14, i2.getMediumValue(), 0.0)
    assertEquals("[limit2: 3.14, 3.14]", i2.toString)
  }

  @Test def OperatorTest = {
    val u1 = UnaryOp("-", Constant(10.0))
    assertEquals("-", u1.operator)
    assertEquals(Constant(10.0), u1.arg)
    assertEquals("-(10.0)", u1.toString)

    val b1 = BinaryOp("+", Constant(5.0), Constant(15.0))
    assertEquals("+", b1.operator)
    assertEquals(Constant(5.0), b1.lhs)
    assertEquals(Constant(15.0), b1.rhs)
    assertEquals("(5.0 + 15.0)", b1.toString)

    val a1 = AggregateOp("min", Constant(1.0) :: Constant(2.0) :: Constant(3.0) :: Nil)
    assertEquals("min", a1.operator)
    assertEquals(Constant(1.0) :: Constant(2.0) :: Constant(3.0) :: Nil, a1.args)
    assertEquals("min(1.0, 2.0, 3.0)", a1.toString)
  }

  @Test def ProblemElementTest = {

    // max: x1
    val g1 = Goal("profit", Variable("x1"), true)
    assertEquals("profit", g1.name)
    assertEquals(Variable("x1"), g1.expr)
    assertEquals(true, g1.max)
    assertEquals("max profit: x1", g1.toString)

    // x2 <= 15
    val c1 = Constraint("limit", Variable("x2"), Constant(15), false)
    assertEquals("limit", c1.name)
    assertEquals(Variable("x2"), c1.lhs)
    assertEquals(Constant(15), c1.rhs)
    assertEquals(false, c1.gte)
    assertEquals("limit: x2 <= 15.0", c1.toString)

    // Variable a[0, 10]
    val v1 = VariableDef("a", 0, 10)
    assertEquals("a", v1.name)
    assertEquals(0.0, v1.min, 0.0)
    assertEquals(10.0, v1.max, 0.0)
    assertEquals("var[0.0, 10.0] a", v1.toString)

    // Supposed utility function *dec: z
    val u1 = UtilityFunction(Variable("z"))
    assertEquals(Variable("z"), u1.expr)
    assertEquals("!dec: z", u1.toString)
  }

}