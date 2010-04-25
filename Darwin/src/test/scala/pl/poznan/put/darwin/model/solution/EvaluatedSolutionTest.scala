package pl.poznan.put.darwin.model.solution

import pl.poznan.put.darwin.model.problem._
import pl.poznan.put.darwin.model.Config
import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.junit.Test
import org.scalatest.Suite

class EvaluatedSolutionTest extends Suite {

  @Test def evaluationTest() {
    val goalMax = Goal("z", Variable("z"), true)
    val goalMin = Goal("y", Variable("y"), false)

    val problem = new Problem("foomybar",
      List(VariableDef("z", 0, 200), VariableDef("y", 0, 200)),
      List(goalMax, goalMin),
      UtilityFunction(BinaryOp("+", Variable("z"), BinaryOp("+", Variable("y"), Constant(30.0)))),
      List()
    )

    val perfs = List(10.0, 2.0, 7.0, 4.0, 5.0, 6.0, 8.0, 3.0, 9.0, 1.0)
    val goals = Map(goalMax -> perfs.sort((a, b) => a < b), goalMin -> perfs.sort((a, b) => a < b).reverse)


    // Test EvaluatedSolution itself
    val evaluated = new EvaluatedSolution(problem, Map(), goals)

    assertEquals(problem, evaluated.problem)
    assertEquals(Map(), evaluated.values)
    assertEquals(goals, evaluated.performances)
    Config.USE_AVG = false
    assertEquals( 1.0, evaluated.getPercentile(goalMax,   0.0), 0.0)
    assertEquals( 1.0, evaluated.getPercentile(goalMax,  10.0), 0.0)
    assertEquals( 2.0, evaluated.getPercentile(goalMax,  20.0), 0.0)
    assertEquals( 3.0, evaluated.getPercentile(goalMax,  30.0), 0.0)
    assertEquals( 4.0, evaluated.getPercentile(goalMax,  40.0), 0.0)
    assertEquals( 5.0, evaluated.getPercentile(goalMax,  50.0), 0.0)
    assertEquals( 6.0, evaluated.getPercentile(goalMax,  60.0), 0.0)
    assertEquals( 7.0, evaluated.getPercentile(goalMax,  70.0), 0.0)
    assertEquals( 8.0, evaluated.getPercentile(goalMax,  80.0), 0.0)
    assertEquals( 9.0, evaluated.getPercentile(goalMax,  90.0), 0.0)
    assertEquals(10.0, evaluated.getPercentile(goalMax, 100.0), 0.0)

    assertEquals(10.0, evaluated.getPercentile(goalMin,   0.0), 0.0)
    assertEquals(10.0, evaluated.getPercentile(goalMin,  10.0), 0.0)
    assertEquals( 9.0, evaluated.getPercentile(goalMin,  20.0), 0.0)
    assertEquals( 8.0, evaluated.getPercentile(goalMin,  30.0), 0.0)
    assertEquals( 7.0, evaluated.getPercentile(goalMin,  40.0), 0.0)
    assertEquals( 6.0, evaluated.getPercentile(goalMin,  50.0), 0.0)
    assertEquals( 5.0, evaluated.getPercentile(goalMin,  60.0), 0.0)
    assertEquals( 4.0, evaluated.getPercentile(goalMin,  70.0), 0.0)
    assertEquals( 3.0, evaluated.getPercentile(goalMin,  80.0), 0.0)
    assertEquals( 2.0, evaluated.getPercentile(goalMin,  90.0), 0.0)
    assertEquals( 1.0, evaluated.getPercentile(goalMin, 100.0), 0.0)

    Config.USE_AVG = true
    assertEquals(1.0, evaluated.getPercentile(goalMax,   0.0), 0.0)
    assertEquals(1.0, evaluated.getPercentile(goalMax,  10.0), 0.0)
    assertEquals(1.5, evaluated.getPercentile(goalMax,  20.0), 0.0)
    assertEquals(2.0, evaluated.getPercentile(goalMax,  30.0), 0.0)
    assertEquals(2.5, evaluated.getPercentile(goalMax,  40.0), 0.0)
    assertEquals(3.0, evaluated.getPercentile(goalMax,  50.0), 0.0)
    assertEquals(3.5, evaluated.getPercentile(goalMax,  60.0), 0.0)
    assertEquals(4.0, evaluated.getPercentile(goalMax,  70.0), 0.0)
    assertEquals(4.5, evaluated.getPercentile(goalMax,  80.0), 0.0)
    assertEquals(5.0, evaluated.getPercentile(goalMax,  90.0), 0.0)
    assertEquals(5.5, evaluated.getPercentile(goalMax, 100.0), 0.0)

    assertEquals(10.0, evaluated.getPercentile(goalMin,   0.0), 0.0)
    assertEquals(10.0, evaluated.getPercentile(goalMin,  10.0), 0.0)
    assertEquals( 9.5, evaluated.getPercentile(goalMin,  20.0), 0.0)
    assertEquals( 9.0, evaluated.getPercentile(goalMin,  30.0), 0.0)
    assertEquals( 8.5, evaluated.getPercentile(goalMin,  40.0), 0.0)
    assertEquals( 8.0, evaluated.getPercentile(goalMin,  50.0), 0.0)
    assertEquals( 7.5, evaluated.getPercentile(goalMin,  60.0), 0.0)
    assertEquals( 7.0, evaluated.getPercentile(goalMin,  70.0), 0.0)
    assertEquals( 6.5, evaluated.getPercentile(goalMin,  80.0), 0.0)
    assertEquals( 6.0, evaluated.getPercentile(goalMin,  90.0), 0.0)
    assertEquals( 5.5, evaluated.getPercentile(goalMin, 100.0), 0.0)


    // Test companion object acting as factory here
    val i = Interval("i", 0, 10)

    val goalMax2 = Goal("z", BinaryOp("*", i, Variable("z")), true)
    val goalMin2 = Goal("y", BinaryOp("*", i, Variable("y")), false)

    val problem2 = new Problem("foomybar",
      List(VariableDef("z", 0, 200), VariableDef("y", 0, 200)),
      List(goalMax2, goalMin2),
      UtilityFunction(
        BinaryOp("+",
          Variable("z"),
          BinaryOp("+", Variable("y"), Constant(30.0))
        )
      ),
      List()
    )


    val s = Solution(problem2, Map("z" -> 1, "y" -> 1))
    val scenarios = perfs.map[Map[String, Double]](n => Map("i" -> n))
    val evaluated2 = EvaluatedSolution(s, scenarios)

    val goals2 = Map(goalMax2 -> perfs.sort((a, b) => a < b), goalMin2 -> perfs.sort((a, b) => a < b).reverse)

    assertEquals(problem2, evaluated2.problem)
    assertEquals(Map("z" -> 1, "y" -> 1), evaluated2.values)
    assertEquals(goals2, evaluated2.performances)
    assertEquals(41.0, evaluated2.utilityFunctionValue, 0.0)
    Config.USE_AVG = false
    assertEquals( 1.0, evaluated2.getPercentile(goalMax2,   0.0), 0.0)
    assertEquals( 1.0, evaluated2.getPercentile(goalMax2,  10.0), 0.0)
    assertEquals( 2.0, evaluated2.getPercentile(goalMax2,  20.0), 0.0)
    assertEquals( 3.0, evaluated2.getPercentile(goalMax2,  30.0), 0.0)
    assertEquals( 4.0, evaluated2.getPercentile(goalMax2,  40.0), 0.0)
    assertEquals( 5.0, evaluated2.getPercentile(goalMax2,  50.0), 0.0)
    assertEquals( 6.0, evaluated2.getPercentile(goalMax2,  60.0), 0.0)
    assertEquals( 7.0, evaluated2.getPercentile(goalMax2,  70.0), 0.0)
    assertEquals( 8.0, evaluated2.getPercentile(goalMax2,  80.0), 0.0)
    assertEquals( 9.0, evaluated2.getPercentile(goalMax2,  90.0), 0.0)
    assertEquals(10.0, evaluated2.getPercentile(goalMax2, 100.0), 0.0)

    assertEquals(10.0, evaluated2.getPercentile(goalMin2,   0.0), 0.0)
    assertEquals(10.0, evaluated2.getPercentile(goalMin2,  10.0), 0.0)
    assertEquals( 9.0, evaluated2.getPercentile(goalMin2,  20.0), 0.0)
    assertEquals( 8.0, evaluated2.getPercentile(goalMin2,  30.0), 0.0)
    assertEquals( 7.0, evaluated2.getPercentile(goalMin2,  40.0), 0.0)
    assertEquals( 6.0, evaluated2.getPercentile(goalMin2,  50.0), 0.0)
    assertEquals( 5.0, evaluated2.getPercentile(goalMin2,  60.0), 0.0)
    assertEquals( 4.0, evaluated2.getPercentile(goalMin2,  70.0), 0.0)
    assertEquals( 3.0, evaluated2.getPercentile(goalMin2,  80.0), 0.0)
    assertEquals( 2.0, evaluated2.getPercentile(goalMin2,  90.0), 0.0)
    assertEquals( 1.0, evaluated2.getPercentile(goalMin2, 100.0), 0.0)

    Config.USE_AVG = true
    assertEquals(1.0, evaluated2.getPercentile(goalMax2,   0.0), 0.0)
    assertEquals(1.0, evaluated2.getPercentile(goalMax2,  10.0), 0.0)
    assertEquals(1.5, evaluated2.getPercentile(goalMax2,  20.0), 0.0)
    assertEquals(2.0, evaluated2.getPercentile(goalMax2,  30.0), 0.0)
    assertEquals(2.5, evaluated2.getPercentile(goalMax2,  40.0), 0.0)
    assertEquals(3.0, evaluated2.getPercentile(goalMax2,  50.0), 0.0)
    assertEquals(3.5, evaluated2.getPercentile(goalMax2,  60.0), 0.0)
    assertEquals(4.0, evaluated2.getPercentile(goalMax2,  70.0), 0.0)
    assertEquals(4.5, evaluated2.getPercentile(goalMax2,  80.0), 0.0)
    assertEquals(5.0, evaluated2.getPercentile(goalMax2,  90.0), 0.0)
    assertEquals(5.5, evaluated2.getPercentile(goalMax2, 100.0), 0.0)

    assertEquals(10.0, evaluated2.getPercentile(goalMin2,   0.0), 0.0)
    assertEquals(10.0, evaluated2.getPercentile(goalMin2,  10.0), 0.0)
    assertEquals( 9.5, evaluated2.getPercentile(goalMin2,  20.0), 0.0)
    assertEquals( 9.0, evaluated2.getPercentile(goalMin2,  30.0), 0.0)
    assertEquals( 8.5, evaluated2.getPercentile(goalMin2,  40.0), 0.0)
    assertEquals( 8.0, evaluated2.getPercentile(goalMin2,  50.0), 0.0)
    assertEquals( 7.5, evaluated2.getPercentile(goalMin2,  60.0), 0.0)
    assertEquals( 7.0, evaluated2.getPercentile(goalMin2,  70.0), 0.0)
    assertEquals( 6.5, evaluated2.getPercentile(goalMin2,  80.0), 0.0)
    assertEquals( 6.0, evaluated2.getPercentile(goalMin2,  90.0), 0.0)
    assertEquals( 5.5, evaluated2.getPercentile(goalMin2, 100.0), 0.0)
  }
}