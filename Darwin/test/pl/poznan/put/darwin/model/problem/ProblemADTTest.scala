package pl.poznan.put.darwin.model.problem

class ProblemADTTest {
  import org.junit._, Assert._

  @Test def LeafTest = {
    val c1 = Constant(-15)
    assertEquals(-15.0, c1.value, 0.0)

    val v1 = Variable("x1", 0, 1)
    assertEquals("x1", v1.name)
    assertEquals(0.0, v1.min, 0.0)
    assertEquals(1.0, v1.max, 0.0)

    val i1 = Interval("limit1", -1, 10)
    assertEquals("limit1", i1.name)
    assertEquals(-1.0, i1.lower, 0.0)
    assertEquals(10.0, i1.upper, 0.0)
    assertEquals(4.5, i1.getMiddleValue(), 0.0)

    val i2 = Interval("limit2", 3.14, 3.14)
    assertEquals("limit2", i2.name)
    assertEquals(3.14, i2.lower, 0.0)
    assertEquals(3.14, i2.upper, 0.0)
    assertEquals(3.14, i2.getMiddleValue(), 0.0)
  }

  @Test def OperatorTest = {
    val u1 = UnaryOp("-", Constant(10.0))
    assertEquals("-", u1.operator)
    assertEquals(Constant(10.0), u1.arg)

    val b1 = BinaryOp("+", Constant(5.0), Constant(15.0))
    assertEquals("+", b1.operator)
    assertEquals(Constant(5.0), b1.lhs)
    assertEquals(Constant(15.0), b1.rhs)
  }

}