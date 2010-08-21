package pl.poznan.put.darwin.evolution

import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import pl.poznan.put.darwin.ProblemRepository
import pl.poznan.put.darwin.model.solution.Solution

class CrossOverTest extends JUnitSuite with ProblemRepository {

  @Test def crossOverTest1 = {
    // It'll be quite hard to test crossover because is random. We'll
    // just create child few times and see if it is feasible and stays
    // between parents' values
    val a = Map("x1" -> 25.0, "x2" ->  1.0)
    val b = Map("x1" ->  5.0, "x2" -> 15.0)

    for (idx <- 1 to 10) {
      val c = CrossOver(new Solution(trainsSoldiersNoIntervalsSim, a),
          new Solution(trainsSoldiersNoIntervalsSim, b), List())
      assertTrue(c.values("x1") >= 5.0)
      assertTrue(c.values("x1") <= 25.0)
      assertTrue(c.values("x2") >= 1.0)
      assertTrue(c.values("x2") <= 15.0)
      assertTrue(c.values("x1") != a("x1"))
      assertTrue(c.values("x2") != a("x2"))
      assertTrue(c.values("x1") != b("x1"))
      assertTrue(c.values("x2") != b("x2"))
      assertTrue(c.isFeasibleOnScenarios(List()))
    }
    
  }

  @Test def crossOverTest2 = {
    // This time we'll use problem with integer constraints
    val a = Map("x1" -> 2.0, "x2" -> 20.0)
    val b = Map("x1" -> 20.0, "x2" -> 2.0)

    var theSame = 0
    for (idx <- 1 to 10) {
      val c = CrossOver(new Solution(integerTrainsSoldiersNoIntervalsSim, a),
          new Solution(integerTrainsSoldiersNoIntervalsSim, b), List())
      assertTrue(c.values("x1") >= 2)
      assertTrue(c.values("x1") <= 20)
      assertTrue(c.values("x2") >= 2)
      assertTrue(c.values("x2") <= 20)
      if (c.values("x1") != a("x1")) {
        theSame += 1
      } else if (c.values("x1") != b("x1")) {
        theSame += 1
      } else if (c.values("x2") != a("x2")) {
        theSame += 1
      } else if (c.values("x2") != b("x2")) {
        theSame += 1
      }
      assertEquals(math.round(c.values("x1")), c.values("x1"), 0.0)
      assertEquals(math.round(c.values("x2")), c.values("x2"), 0.0)
      assertTrue(c.isFeasibleOnScenarios(List()))
    }
    assertTrue(theSame < 20)
    
  }
}
