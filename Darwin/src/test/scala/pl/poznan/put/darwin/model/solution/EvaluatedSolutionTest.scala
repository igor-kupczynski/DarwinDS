package pl.poznan.put.darwin.model.solution

import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import pl.poznan.put.darwin.ProblemRepository
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem._
import pl.poznan.put.darwin.simulation.Simulation

class EvaluatedSolutionTest extends JUnitSuite with ProblemRepository {

  @Test def evaluationTest() {
    val goalMax = Goal("z", Variable("z"), true)
    val goalMin = Goal("y", Variable("y"), false)

    val problem = new Problem("foomybar",
      List(VariableDef("z", 0, 200), VariableDef("y", 0, 200)),
      List(goalMax, goalMin),
      UtilityFunction(BinaryOp("+", Variable("z"), BinaryOp("+", Variable("y"), Constant(30.0)))),
      List()
    )
    val sim = new Simulation(defaultConfig, problem)
    val simAvg = new Simulation(configWithAvg, problem)

    val perfs = List(10.0, 2.0, 7.0, 4.0, 5.0, 6.0, 8.0, 3.0, 9.0, 1.0)
    val goals = Map(goalMax -> perfs.sortWith((a, b) => a < b), goalMin -> perfs.sortWith((a, b) => a < b).reverse)


    // Test EvaluatedSolution itself
    val evaluated = new EvaluatedSolution(sim, Map(), goals)

    assertEquals(sim, evaluated.sim)
    assertEquals(Map(), evaluated.values)
    assertEquals(goals, evaluated.performances)
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

    
    val evaluatedAvg = new EvaluatedSolution(simAvg, Map(), goals)
    assertEquals(1.0, evaluatedAvg.getPercentile(goalMax,   0.0), 0.0)
    assertEquals(1.0, evaluatedAvg.getPercentile(goalMax,  10.0), 0.0)
    assertEquals(1.5, evaluatedAvg.getPercentile(goalMax,  20.0), 0.0)
    assertEquals(2.0, evaluatedAvg.getPercentile(goalMax,  30.0), 0.0)
    assertEquals(2.5, evaluatedAvg.getPercentile(goalMax,  40.0), 0.0)
    assertEquals(3.0, evaluatedAvg.getPercentile(goalMax,  50.0), 0.0)
    assertEquals(3.5, evaluatedAvg.getPercentile(goalMax,  60.0), 0.0)
    assertEquals(4.0, evaluatedAvg.getPercentile(goalMax,  70.0), 0.0)
    assertEquals(4.5, evaluatedAvg.getPercentile(goalMax,  80.0), 0.0)
    assertEquals(5.0, evaluatedAvg.getPercentile(goalMax,  90.0), 0.0)
    assertEquals(5.5, evaluatedAvg.getPercentile(goalMax, 100.0), 0.0)

    assertEquals(10.0, evaluatedAvg.getPercentile(goalMin,   0.0), 0.0)
    assertEquals(10.0, evaluatedAvg.getPercentile(goalMin,  10.0), 0.0)
    assertEquals( 9.5, evaluatedAvg.getPercentile(goalMin,  20.0), 0.0)
    assertEquals( 9.0, evaluatedAvg.getPercentile(goalMin,  30.0), 0.0)
    assertEquals( 8.5, evaluatedAvg.getPercentile(goalMin,  40.0), 0.0)
    assertEquals( 8.0, evaluatedAvg.getPercentile(goalMin,  50.0), 0.0)
    assertEquals( 7.5, evaluatedAvg.getPercentile(goalMin,  60.0), 0.0)
    assertEquals( 7.0, evaluatedAvg.getPercentile(goalMin,  70.0), 0.0)
    assertEquals( 6.5, evaluatedAvg.getPercentile(goalMin,  80.0), 0.0)
    assertEquals( 6.0, evaluatedAvg.getPercentile(goalMin,  90.0), 0.0)
    assertEquals( 5.5, evaluatedAvg.getPercentile(goalMin, 100.0), 0.0)


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
    val sim2 = new Simulation(defaultConfig, problem2)
    val sim2Avg = new Simulation(configWithAvg, problem2)


    val s = Solution(sim2, Map("z" -> 1, "y" -> 1))
    val sAvg = Solution(sim2Avg, Map("z" -> 1, "y" -> 1))
    val scenarios = perfs.map(n => Map("i" -> n))
    val evaluated2 = EvaluatedSolution(s, scenarios)
    val evaluated2Avg = EvaluatedSolution(sAvg, scenarios)

    val goals2 = Map(goalMax2 -> perfs.sortWith((a, b) => a < b),
      goalMin2 -> perfs.sortWith((a, b) => a < b).reverse)

    assertEquals(sim2, evaluated2.sim)
    assertEquals(Map("z" -> 1, "y" -> 1), evaluated2.values)
    assertEquals(goals2, evaluated2.performances)
    assertEquals(41.0, evaluated2.utilityFunctionValue, 0.0)
    
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

    assertEquals(1.0, evaluated2Avg.getPercentile(goalMax2,   0.0), 0.0)
    assertEquals(1.0, evaluated2Avg.getPercentile(goalMax2,  10.0), 0.0)
    assertEquals(1.5, evaluated2Avg.getPercentile(goalMax2,  20.0), 0.0)
    assertEquals(2.0, evaluated2Avg.getPercentile(goalMax2,  30.0), 0.0)
    assertEquals(2.5, evaluated2Avg.getPercentile(goalMax2,  40.0), 0.0)
    assertEquals(3.0, evaluated2Avg.getPercentile(goalMax2,  50.0), 0.0)
    assertEquals(3.5, evaluated2Avg.getPercentile(goalMax2,  60.0), 0.0)
    assertEquals(4.0, evaluated2Avg.getPercentile(goalMax2,  70.0), 0.0)
    assertEquals(4.5, evaluated2Avg.getPercentile(goalMax2,  80.0), 0.0)
    assertEquals(5.0, evaluated2Avg.getPercentile(goalMax2,  90.0), 0.0)
    assertEquals(5.5, evaluated2Avg.getPercentile(goalMax2, 100.0), 0.0)

    assertEquals(10.0, evaluated2Avg.getPercentile(goalMin2,   0.0), 0.0)
    assertEquals(10.0, evaluated2Avg.getPercentile(goalMin2,  10.0), 0.0)
    assertEquals( 9.5, evaluated2Avg.getPercentile(goalMin2,  20.0), 0.0)
    assertEquals( 9.0, evaluated2Avg.getPercentile(goalMin2,  30.0), 0.0)
    assertEquals( 8.5, evaluated2Avg.getPercentile(goalMin2,  40.0), 0.0)
    assertEquals( 8.0, evaluated2Avg.getPercentile(goalMin2,  50.0), 0.0)
    assertEquals( 7.5, evaluated2Avg.getPercentile(goalMin2,  60.0), 0.0)
    assertEquals( 7.0, evaluated2Avg.getPercentile(goalMin2,  70.0), 0.0)
    assertEquals( 6.5, evaluated2Avg.getPercentile(goalMin2,  80.0), 0.0)
    assertEquals( 6.0, evaluated2Avg.getPercentile(goalMin2,  90.0), 0.0)
    assertEquals( 5.5, evaluated2Avg.getPercentile(goalMin2, 100.0), 0.0)


    // Check if Evaluating multiple solutions at one time gives the same result
    val evaledList = EvaluatedSolution(List(s), scenarios)
    assertEquals(evaluated2, evaledList(0))
  }
}
