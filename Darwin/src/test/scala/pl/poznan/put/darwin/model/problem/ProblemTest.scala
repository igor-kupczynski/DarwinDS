package pl.poznan.put.darwin.model.problem
import org.specs.runner.ScalaTest
import org.specs.{Specification, SpecificationWithJUnit}

class ProblemTest extends Specification with ScalaTest {

  var simpleNoIntervals: Problem = _
  var simpleWithIntervals: Problem = _
  var trainsSoldiersNoIntervals: Problem = _

  "Problem" should {
    doPreparations()
    "return empty default scenario if no intervals" in {
      simpleNoIntervals.getDefaultScenario must be_==(Map())
      trainsSoldiersNoIntervals.getDefaultScenario must be_==(Map())
    }
    "return default scenario with medium values for intervals" in {
      val expected: Map[String, Double] = Map("i1" -> 1.0)
      simpleWithIntervals.getDefaultScenario must be_==(expected)
    }
    "return intervals that it contains" in {
      simpleNoIntervals.getIntervals() must be_==(Nil)
      simpleWithIntervals.getIntervals() must be_==(List(Interval("i1", 0.9, 1.1)))
      trainsSoldiersNoIntervals.getIntervals() must be_==(Nil)
    }
    "return variables that it contains" in {
      simpleNoIntervals.getVariables() must be_==(List(VariableDef("x", 0, 200)))
      simpleWithIntervals.getVariables() must be_==(List(VariableDef("x", 0, 200)))
      trainsSoldiersNoIntervals.getVariables() must
              be_==(List(VariableDef("x1", 0, 200), VariableDef("x2", 0, 200)))
    }
    "print nice decription using toString method" in {
    var expected = "" +
      "var[0.0, 200.0] x;\n\n" +
      "max profit: x;\n\n" +
      "!dec: profit;\n\n" +
      "non-zero: x >= 0.0;\n" +
      "limit: x <= 100.0;\n"
    simpleNoIntervals.toString must be_==(expected)

    expected = "" +
      "var[0.0, 200.0] x;\n\n" +
      "max profit: x;\n\n" +
      "!dec: profit;\n\n" +
      "non-zero: x >= 0.0;\n" +
      "limit: ([i1: 0.9, 1.1] * x) <= 100.0;\n"
    simpleWithIntervals.toString must be_==(expected)

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
     trainsSoldiersNoIntervals.toString must be_==(expected)
    }

  }


  def doPreparations() {
    val x = Variable("x")
    val zero = Constant(0)
    val max = Constant(100)
    val i = Interval("i1", 0.9, 1.1)


    simpleNoIntervals = new Problem("Simple, no intervals",
          VariableDef("x", 0, 200) :: Nil,
          Goal("profit", x, true) :: Nil,
          UtilityFunction(Variable("profit")),
          Constraint("non-zero", x, zero, true) :: Constraint("limit", x, max, false) :: Nil)

    simpleWithIntervals = new Problem("Simple, with intervals",
          VariableDef("x", 0, 200) :: Nil,
          Goal("profit", x, true) :: Nil,
          UtilityFunction(Variable("profit")),
          Constraint("non-zero", x, zero, true) :: Constraint("limit", BinaryOp("*", i, x), max, false) :: Nil)

    trainsSoldiersNoIntervals = Parser.ProblemParser.parse(
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
  }
}