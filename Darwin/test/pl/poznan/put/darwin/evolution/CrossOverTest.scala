package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.solution.Solution
import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite

class CrossOverTest extends JUnitSuite with BaseEvolution {

  @Test def crossOverTest = {
    // It'll be quite hard to test crossover because is random. We'll
    // just create child few times and see if it is feasible and stays
    // between parents' values
    val a = Map("x1" -> 0.0, "x2" -> 10.0)
    val b = Map("x1" -> 10.0, "x2" -> 0.0)

    for (idx <- 1 to 10) {
      val c = CrossOver(new Solution(trainsSoldiersNoIntervals, a),
          new Solution(trainsSoldiersNoIntervals, b))
      assertTrue(c.values("x1") >= 0)
      assertTrue(c.values("x1") <= 10)
      assertTrue(c.values("x2") >= 0)
      assertTrue(c.values("x2") <= 10)
      assertTrue(c.values("x1") != a("x1"))
      assertTrue(c.values("x2") != a("x2"))
      assertTrue(c.values("x1") != b("x1"))
      assertTrue(c.values("x2") != b("x2"))
      assertTrue(trainsSoldiersNoIntervals.isFeasible(c))
    }
    
  }
}
