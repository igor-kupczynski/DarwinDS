package pl.poznan.put.darwin.model.solution

import pl.poznan.put.darwin.model.problem._
import pl.poznan.put.darwin.model.Config

class EvaluatedSolutionTest extends SolutionTest {
  import org.junit._, Assert._

  @Test def evaluationTest() {
    val goal = Goal("z", Variable("z"), true)

    val pMax = new Problem("foomybar",
      List(VariableDef("z", 0, 200)),
      List(goal),
      UtilityFunction(BinaryOp("+", Variable("z"), Constant(30.0))),
      List()
    )

    val maxPerfs = List(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)
    val maxGoals = Map(goal -> List(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0))

    val evaluatedMax = new EvaluatedSolution(pMax, Map(), maxGoals)

    assertEquals(pMax, evaluatedMax.problem)
    assertEquals(Map(), evaluatedMax.values)
    assertEquals(maxGoals, evaluatedMax.performances)
    Config.USE_AVG = false
    assertEquals( 1.0, evaluatedMax.getPercentile(goal,   0.0), 0.0)
    assertEquals( 1.0, evaluatedMax.getPercentile(goal,  10.0), 0.0)
    assertEquals( 2.0, evaluatedMax.getPercentile(goal,  20.0), 0.0)
    assertEquals( 3.0, evaluatedMax.getPercentile(goal,  30.0), 0.0)
    assertEquals( 4.0, evaluatedMax.getPercentile(goal,  40.0), 0.0)
    assertEquals( 5.0, evaluatedMax.getPercentile(goal,  50.0), 0.0)
    assertEquals( 6.0, evaluatedMax.getPercentile(goal,  60.0), 0.0)
    assertEquals( 7.0, evaluatedMax.getPercentile(goal,  70.0), 0.0)
    assertEquals( 8.0, evaluatedMax.getPercentile(goal,  80.0), 0.0)
    assertEquals( 9.0, evaluatedMax.getPercentile(goal,  90.0), 0.0)
    assertEquals(10.0, evaluatedMax.getPercentile(goal, 100.0), 0.0)
    Config.USE_AVG = true
    assertEquals(1.0, evaluatedMax.getPercentile(goal,   0.0), 0.0)
    assertEquals(1.0, evaluatedMax.getPercentile(goal,  10.0), 0.0)
    assertEquals(1.5, evaluatedMax.getPercentile(goal,  20.0), 0.0)
    assertEquals(2.0, evaluatedMax.getPercentile(goal,  30.0), 0.0)
    assertEquals(2.5, evaluatedMax.getPercentile(goal,  40.0), 0.0)
    assertEquals(3.0, evaluatedMax.getPercentile(goal,  50.0), 0.0)
    assertEquals(3.5, evaluatedMax.getPercentile(goal,  60.0), 0.0)
    assertEquals(4.0, evaluatedMax.getPercentile(goal,  70.0), 0.0)
    assertEquals(4.5, evaluatedMax.getPercentile(goal,  80.0), 0.0)
    assertEquals(5.0, evaluatedMax.getPercentile(goal,  90.0), 0.0)
    assertEquals(5.5, evaluatedMax.getPercentile(goal, 100.0), 0.0)
  }
}