package pl.poznan.put.darwin.model.solution

import pl.poznan.put.darwin.model.problem._
import pl.poznan.put.darwin.model.Config

class EvaluatedSolutionTest extends SolutionTest {
  import org.junit._, Assert._

  @Test def evaluationTest() {
    val goalMax = Goal("z", Variable("z"), true)
    val goalMin = Goal("y", Variable("y"), true)

    val problem = new Problem("foomybar",
      List(VariableDef("z", 0, 200), VariableDef("y", 0, 200)),
      List(goalMax, goalMin),
      UtilityFunction(BinaryOp("+", Variable("z"), BinaryOp("+", Variable("y"), Constant(30.0)))),
      List()
    )

    val perfs = List(10.0, 2.0, 7.0, 4.0, 5.0, 6.0, 8.0, 3.0, 9.0, 1.0)
    val goals = Map(goalMax -> perfs.sort((a, b) => a < b), goalMin -> perfs.sort((a, b) => a < b).reverse)


    // Test EvaluatedSolution itself
    val evaluatedMax = new EvaluatedSolution(problem, Map(), goals)

    assertEquals(problem, evaluatedMax.problem)
    assertEquals(Map(), evaluatedMax.values)
    assertEquals(goals, evaluatedMax.performances)
    Config.USE_AVG = false
    assertEquals( 1.0, evaluatedMax.getPercentile(goalMax,   0.0), 0.0)
    assertEquals( 1.0, evaluatedMax.getPercentile(goalMax,  10.0), 0.0)
    assertEquals( 2.0, evaluatedMax.getPercentile(goalMax,  20.0), 0.0)
    assertEquals( 3.0, evaluatedMax.getPercentile(goalMax,  30.0), 0.0)
    assertEquals( 4.0, evaluatedMax.getPercentile(goalMax,  40.0), 0.0)
    assertEquals( 5.0, evaluatedMax.getPercentile(goalMax,  50.0), 0.0)
    assertEquals( 6.0, evaluatedMax.getPercentile(goalMax,  60.0), 0.0)
    assertEquals( 7.0, evaluatedMax.getPercentile(goalMax,  70.0), 0.0)
    assertEquals( 8.0, evaluatedMax.getPercentile(goalMax,  80.0), 0.0)
    assertEquals( 9.0, evaluatedMax.getPercentile(goalMax,  90.0), 0.0)
    assertEquals(10.0, evaluatedMax.getPercentile(goalMax, 100.0), 0.0)

    assertEquals(10.0, evaluatedMax.getPercentile(goalMin,   0.0), 0.0)
    assertEquals(10.0, evaluatedMax.getPercentile(goalMin,  10.0), 0.0)
    assertEquals( 9.0, evaluatedMax.getPercentile(goalMin,  20.0), 0.0)
    assertEquals( 8.0, evaluatedMax.getPercentile(goalMin,  30.0), 0.0)
    assertEquals( 7.0, evaluatedMax.getPercentile(goalMin,  40.0), 0.0)
    assertEquals( 6.0, evaluatedMax.getPercentile(goalMin,  50.0), 0.0)
    assertEquals( 5.0, evaluatedMax.getPercentile(goalMin,  60.0), 0.0)
    assertEquals( 4.0, evaluatedMax.getPercentile(goalMin,  70.0), 0.0)
    assertEquals( 3.0, evaluatedMax.getPercentile(goalMin,  80.0), 0.0)
    assertEquals( 2.0, evaluatedMax.getPercentile(goalMin,  90.0), 0.0)
    assertEquals( 1.0, evaluatedMax.getPercentile(goalMin, 100.0), 0.0)

    Config.USE_AVG = true
    assertEquals(1.0, evaluatedMax.getPercentile(goalMax,   0.0), 0.0)
    assertEquals(1.0, evaluatedMax.getPercentile(goalMax,  10.0), 0.0)
    assertEquals(1.5, evaluatedMax.getPercentile(goalMax,  20.0), 0.0)
    assertEquals(2.0, evaluatedMax.getPercentile(goalMax,  30.0), 0.0)
    assertEquals(2.5, evaluatedMax.getPercentile(goalMax,  40.0), 0.0)
    assertEquals(3.0, evaluatedMax.getPercentile(goalMax,  50.0), 0.0)
    assertEquals(3.5, evaluatedMax.getPercentile(goalMax,  60.0), 0.0)
    assertEquals(4.0, evaluatedMax.getPercentile(goalMax,  70.0), 0.0)
    assertEquals(4.5, evaluatedMax.getPercentile(goalMax,  80.0), 0.0)
    assertEquals(5.0, evaluatedMax.getPercentile(goalMax,  90.0), 0.0)
    assertEquals(5.5, evaluatedMax.getPercentile(goalMax, 100.0), 0.0)

    assertEquals(10.0, evaluatedMax.getPercentile(goalMin,   0.0), 0.0)
    assertEquals(10.0, evaluatedMax.getPercentile(goalMin,  10.0), 0.0)
    assertEquals( 9.5, evaluatedMax.getPercentile(goalMin,  20.0), 0.0)
    assertEquals( 9.0, evaluatedMax.getPercentile(goalMin,  30.0), 0.0)
    assertEquals( 8.5, evaluatedMax.getPercentile(goalMin,  40.0), 0.0)
    assertEquals( 8.0, evaluatedMax.getPercentile(goalMin,  50.0), 0.0)
    assertEquals( 7.5, evaluatedMax.getPercentile(goalMin,  60.0), 0.0)
    assertEquals( 7.0, evaluatedMax.getPercentile(goalMin,  70.0), 0.0)
    assertEquals( 6.5, evaluatedMax.getPercentile(goalMin,  80.0), 0.0)
    assertEquals( 6.0, evaluatedMax.getPercentile(goalMin,  90.0), 0.0)
    assertEquals( 5.5, evaluatedMax.getPercentile(goalMin, 100.0), 0.0)
  }
}