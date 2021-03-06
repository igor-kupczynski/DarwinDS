package pl.poznan.put.darwin.model.problem
import org.specs.{Specification}

class ProblemTest extends Specification {

  var simpleNoIntervals: Problem = _
  var simpleWithIntervals: Problem = _
  var trainsSoldiersNoIntervals: Problem = _         
  var integerTrainsSoldiersNoIntervals: Problem = _         
  
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
    "return contained variables" in {
      simpleNoIntervals.getVariables() must be_==(List(VariableDef("x", 0, 200, null)))
      simpleNoIntervals.getVariable("x") must be_==(VariableDef("x", 0, 200, null))
      simpleWithIntervals.getVariables() must be_==(List(VariableDef("x", 0, 200, null)))
      simpleWithIntervals.getVariable("x") must be_==(VariableDef("x", 0, 200, null))
      integerTrainsSoldiersNoIntervals.getVariables() must
              be_==(List(VariableDef("x1", 0, 200, IntegerConstraint), VariableDef("x2", 0, 200, IntegerConstraint)))
      integerTrainsSoldiersNoIntervals.getVariable("x1") must
              be_==(VariableDef("x1", 0, 200, IntegerConstraint))
      integerTrainsSoldiersNoIntervals.getVariable("x2") must
              be_==(VariableDef("x2", 0, 200, IntegerConstraint))
      integerTrainsSoldiersNoIntervals.getVariable("x3") must
              be(null)
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
  
    expected = "" +
      "var[(I) 0.0, 200.0] x1;\n" +
      "var[(I) 0.0, 200.0] x2;\n\n" +
      "max z: ((3.0 * x1) + (2.0 * x2));\n\n" +
      "!dec: z;\n\n" +
      "Finishing: ((2.0 * x1) + x2) <= 100.0;\n" +
      "Carpentr: (x1 + x2) <= 80.0;\n" +
      "Demand: x1 <= 40.0;\n" +
      "nonZero1: x1 >= ln(1.0);\n" +
      "nonZero2: x2 >= 0.0;\n"
     integerTrainsSoldiersNoIntervals.toString must be_==(expected)
    }

  }


  def doPreparations() {
    val x = Variable("x")
    val zero = Constant(0)
    val max = Constant(100)
    val i = Interval("i1", 0.9, 1.1)


    simpleNoIntervals = new Problem("Simple, no intervals",
          VariableDef("x", 0, 200, null) :: Nil,
          Goal("profit", x, true) :: Nil,
          UtilityFunction(Variable("profit")),
          Constraint("non-zero", x, zero, true) :: Constraint("limit", x, max, false) :: Nil)

    simpleWithIntervals = new Problem("Simple, with intervals",
          VariableDef("x", 0, 200, null) :: Nil,
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

  integerTrainsSoldiersNoIntervals = Parser.ProblemParser.parse(
      "var[(I) 0,200] x1;\n" +
      "var[(I) 0,200] x2;\n" +
      "max z: 3*x1 + 2*x2;\n" +
      "!dec: z;\n" +
      "Finishing: 2*x1 + x2 <= 100;\n" +
      "Carpentr: x1 + x2 <= 80;\n" +
      "Demand: x1 <= 40;\n" +
      "nonZero1: x1 >= ln(1);\n" +
      "nonZero2: x2 >= 0;\n"
    ).get
  }
}
