package pl.poznan.put.darwin.model.problem

import collection.mutable.HashMap

class ProblemTest {

  import org.junit._, Assert._

  val x = Variable("x")
  val zero = Constant(0)
  val max = Constant(200)
  val i = Interval("i1", 0.9, 1.1)

  val SimpleNoIntervals = new Problem("Simple, no intervals",
        VariableDef("x", 0, 200) :: Nil,
        Goal("profit", x, true) :: Nil,
        UtilityFunction(x),
        Constraint("non-zero", x, zero, true) :: Constraint("limit", x, max, false) :: Nil)

  val SimpleWithIntervals = new Problem("Simple, with intervals",
        VariableDef("x", 0, 200) :: Nil,
        Goal("profit", x, true) :: Nil,
        UtilityFunction(x),
        Constraint("non-zero", x, zero, true) :: Constraint("limit", BinaryOp("*", i, x), max, false) :: Nil)

  @Test def getDefaultScenarioTest() {
    assertEquals(new HashMap(), SimpleNoIntervals.getDefaultScenario)
    val expected = new HashMap[String, Double]()
    expected(i.name) = 1.0
    assertEquals(expected, SimpleWithIntervals.getDefaultScenario)
  }

}