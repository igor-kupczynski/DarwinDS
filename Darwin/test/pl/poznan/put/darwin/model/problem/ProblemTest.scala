package pl.poznan.put.darwin.model.problem

import collection.mutable.HashMap

class ProblemTest {

  import org.junit._, Assert._

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
    assertEquals(false, simpleNoIntervals.isFeasible({case "x" => -199.0}))
    assertEquals(false, simpleNoIntervals.isFeasible({case "x" => -1}))
    assertEquals(true, simpleNoIntervals.isFeasible({case "x" => 0}))
    assertEquals(true, simpleNoIntervals.isFeasible({case "x" => 1}))
    assertEquals(true, simpleNoIntervals.isFeasible({case "x" => 50}))
    assertEquals(true, simpleNoIntervals.isFeasible({case "x" => 99}))
    assertEquals(true, simpleNoIntervals.isFeasible({case "x" => 100}))
    assertEquals(false, simpleNoIntervals.isFeasible({case "x" => 101}))
    assertEquals(false, simpleNoIntervals.isFeasible({case "x" => 199}))
    assertEquals(false, simpleNoIntervals.isFeasible({case "x" => 400}))

    assertEquals(false, simpleWithIntervals.isFeasible({case "x" => -199.0}))
    assertEquals(false, simpleWithIntervals.isFeasible({case "x" => -1}))
    assertEquals(true, simpleWithIntervals.isFeasible({case "x" => 0}))
    assertEquals(true, simpleWithIntervals.isFeasible({case "x" => 1}))
    assertEquals(true, simpleWithIntervals.isFeasible({case "x" => 50}))
    assertEquals(true, simpleWithIntervals.isFeasible({case "x" => 99}))
    assertEquals(true, simpleWithIntervals.isFeasible({case "x" => 100}))
    assertEquals(false, simpleWithIntervals.isFeasible({case "x" => 101}))
    assertEquals(false, simpleWithIntervals.isFeasible({case "x" => 199}))
    assertEquals(false, simpleWithIntervals.isFeasible({case "x" => 400}))

    assertEquals(false, trainsSoldiersNoIntervals.isFeasible({case "x1" => -1; case "x2" => -1}))
    assertEquals(true, trainsSoldiersNoIntervals.isFeasible({case "x1" => 1; case "x2" => 1}))
    assertEquals(true, trainsSoldiersNoIntervals.isFeasible({case "x1" => 40; case "x2" => 1}))
    assertEquals(false, trainsSoldiersNoIntervals.isFeasible({case "x1" => 41; case "x2" => 1}))
    assertEquals(false, trainsSoldiersNoIntervals.isFeasible({case "x1" => 198; case "x2" => 168}))
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

  @Test def evaluateTest() {
    var result = simpleNoIntervals.evaluate(emptyScenario, {case "x" => 100})
    assertEquals(100, result.values.next, 0)

    result = simpleNoIntervals.evaluate(emptyScenario, {case "x" => 20})
    assertEquals(20, result.values.next, 0)

    result = simpleWithIntervals.evaluate({case "i1" => 0.9}, {case "x" => 100})
    assertEquals(100, result.values.next, 0)

    result = trainsSoldiersNoIntervals.evaluate(emptyScenario, {case "x1" => 20; case "x2" => 30})
    assertEquals(120, result.values.next, 0)
  }


  @Test def utilityValueTest() {
    var result = simpleNoIntervals.utilityValue({case "profit" => 100})
    assertEquals(100, result, 0)

    result = simpleNoIntervals.utilityValue({case "profit" => 20})
    assertEquals(20, result, 0)

    result = simpleWithIntervals.utilityValue({case "profit" => 100})
    assertEquals(100, result, 0)

    result = trainsSoldiersNoIntervals.utilityValue({case "z" => 120})
    assertEquals(120, result, 0)
  }
}