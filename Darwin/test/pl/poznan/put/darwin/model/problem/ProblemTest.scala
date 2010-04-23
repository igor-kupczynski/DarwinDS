package pl.poznan.put.darwin.model.problem
import org.junit.Test
import org.junit.Assert._
import collection.mutable.HashMap
import org.scalatest.junit.JUnitSuite

class ProblemTest extends JUnitSuite {

  val x = Variable("x")
  val zero = Constant(0)
  val max = Constant(100)
  val i = Interval("i1", 0.9, 1.1)

  val emptyScenario = new HashMap[String, Double]()

  val simpleNoIntervals = new Problem("Simple, no intervals",
        VariableDef("x", 0, 200) :: Nil,
        Goal("profit", x, true) :: Nil,
        UtilityFunction(Variable("profit")),
        Constraint("non-zero", x, zero, true) :: Constraint("limit", x, max, false) :: Nil)

  val simpleWithIntervals = new Problem("Simple, with intervals",
        VariableDef("x", 0, 200) :: Nil,
        Goal("profit", x, true) :: Nil,
        UtilityFunction(Variable("profit")),
        Constraint("non-zero", x, zero, true) :: Constraint("limit", BinaryOp("*", i, x), max, false) :: Nil)

  val trainsSoldiersNoIntervals = Parser.ProblemParser.parse(
    "var[0,200] x1;\n" +
    "var[0,200] x2;\n" +
    "max z: 3*x1 + 2*x2;\n" +
    "!dec: z;\n" +
    "Finishing: 2*x1 + x2 <= 100;\n" +
    "Carpentr: x1 + x2 <= 80;\n" +
    "Demand: x1 <= 40;\n" +
    "nonZero1: x1 >= 0;\n" +
    "nonZero2: x2 >= 0;\n"
  ).get

  @Test def getDefaultScenarioTest() {
    assertEquals(new HashMap(), simpleNoIntervals.getDefaultScenario)
    assertEquals(new HashMap(), trainsSoldiersNoIntervals.getDefaultScenario)
    val expected = new HashMap[String, Double]()
    expected(i.name) = 1.0
    assertEquals(expected, simpleWithIntervals.getDefaultScenario)
  }

  @Test def isFeasibleTest() {
    assertEquals(false, simpleNoIntervals.isFeasible(Map("x" -> -199.0)))
    assertEquals(false, simpleNoIntervals.isFeasible(Map("x" ->  -1.0)))
    assertEquals(true, simpleNoIntervals.isFeasible(Map("x" ->  0.0)))
    assertEquals(true, simpleNoIntervals.isFeasible(Map("x" ->  1.0)))
    assertEquals(true, simpleNoIntervals.isFeasible(Map("x" ->  50.0)))
    assertEquals(true, simpleNoIntervals.isFeasible(Map("x" ->  99.0)))
    assertEquals(true, simpleNoIntervals.isFeasible(Map("x" ->  100.0)))
    assertEquals(false, simpleNoIntervals.isFeasible(Map("x" ->  101.0)))
    assertEquals(false, simpleNoIntervals.isFeasible(Map("x" ->  199.0)))
    assertEquals(false, simpleNoIntervals.isFeasible(Map("x" ->  400.0)))

    assertEquals(false, simpleWithIntervals.isFeasible(Map("x" ->  -199.0)))
    assertEquals(false, simpleWithIntervals.isFeasible(Map("x" ->  -1.0)))
    assertEquals(true, simpleWithIntervals.isFeasible(Map("x" ->  0.0)))
    assertEquals(true, simpleWithIntervals.isFeasible(Map("x" ->  1.0)))
    assertEquals(true, simpleWithIntervals.isFeasible(Map("x" ->  50.0)))
    assertEquals(true, simpleWithIntervals.isFeasible(Map("x" ->  99.0)))
    assertEquals(true, simpleWithIntervals.isFeasible(Map("x" ->  100.0)))
    assertEquals(false, simpleWithIntervals.isFeasible(Map("x" ->  101.0)))
    assertEquals(false, simpleWithIntervals.isFeasible(Map("x" ->  199.0)))
    assertEquals(false, simpleWithIntervals.isFeasible(Map("x" ->  400.0)))

    assertEquals(false, trainsSoldiersNoIntervals.isFeasible(Map("x1" -> -1.0, "x2" -> -1.0)))
    assertEquals(true, trainsSoldiersNoIntervals.isFeasible(Map("x1" -> 1.0, "x2" -> 1.0)))
    assertEquals(true, trainsSoldiersNoIntervals.isFeasible(Map("x1" -> 40.0, "x2" -> 1.0)))
    assertEquals(false, trainsSoldiersNoIntervals.isFeasible(Map("x1" -> 41.0, "x2" -> 1.0)))
    assertEquals(false, trainsSoldiersNoIntervals.isFeasible(Map("x1" -> 198.0, "x2" -> 168.0)))
  }

  @Test def getIntervalsTest() {
    assertEquals(Nil, simpleNoIntervals.getIntervals())
    assertEquals(i :: Nil, simpleWithIntervals.getIntervals())
    assertEquals(Nil, trainsSoldiersNoIntervals.getIntervals())
  }


  @Test def getVariablesTest() {
    assertEquals(VariableDef("x", 0, 200) :: Nil, simpleNoIntervals.getVariables())
    assertEquals(VariableDef("x", 0, 200) :: Nil, simpleWithIntervals.getVariables())
    assertEquals(VariableDef("x1", 0, 200) :: VariableDef("x2", 0, 200) :: Nil,
      trainsSoldiersNoIntervals.getVariables())
  }


  @Test def toStringTest() {
    var expected = "" +
    "var[0.0, 200.0] x;\n\n" +
    "max profit: x;\n\n" +
    "!dec: profit;\n\n" +
    "non-zero: x >= 0.0;\n" +
    "limit: x <= 100.0;\n"
    assertEquals(expected, simpleNoIntervals.toString)

    expected = "" +
    "var[0.0, 200.0] x;\n\n" +
    "max profit: x;\n\n" +
    "!dec: profit;\n\n" +
    "non-zero: x >= 0.0;\n" +
    "limit: ([i1: 0.9, 1.1] * x) <= 100.0;\n"
    assertEquals(expected, simpleWithIntervals.toString)

    expected = "" +
    "var[0.0, 200.0] x1;\n" +
    "var[0.0, 200.0] x2;\n\n" +
    "max z: ((3.0 * x1) + (2.0 * x2));\n\n" +
    "!dec: z;\n\n" +
    "Finishing: ((2.0 * x1) + x2) <= 100.0;\n" +
    "Carpentr: (x1 + x2) <= 80.0;\n" +
    "Demand: x1 <= 40.0;\n" +
    "nonZero1: x1 >= 0.0;\n" +
    "nonZero2: x2 >= 0.0;\n"
    assertEquals(expected, trainsSoldiersNoIntervals.toString)
  }
}