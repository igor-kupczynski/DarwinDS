package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.solution.RankedSolution
import pl.poznan.put.darwin.model.Config
import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite

class SelectionStrategyTest extends JUnitSuite with ProblemRepository {

  @Test def probabilitiesTest = {
    val solutions: List[RankedSolution] =
      new RankedSolution(trainsSoldiersNoIntervals, Map(), Map(), 0.0, 0.0, 1) ::
      new RankedSolution(trainsSoldiersNoIntervals, Map(), Map(), 0.0, 0.0, 2) ::
      new RankedSolution(trainsSoldiersNoIntervals, Map(), Map(), 0.0, 0.0, 3) ::
      new RankedSolution(trainsSoldiersNoIntervals, Map(), Map(), 0.0, 0.0, 4) ::
      new RankedSolution(trainsSoldiersNoIntervals, Map(), Map(), 0.0, 0.0, 5) ::
      Nil

    val len = solutions.length

    val probabilities = SelectionStrategy.computeProbabilities(solutions)
    assertEquals(len, probabilities.length)
    var acc = 0.0
    for (idx <- 1 to len) {
      val delta = math.pow((1.0 + len - idx) / len, (new Config()).GAMMA) - math.pow((0.0 + len - idx) / len, (new Config()).GAMMA)
      assertTrue(probabilities(idx-1) == acc + delta)
      acc += delta
    }


    val values = List(0.35, 0.63, 0.83, 0.95, 0.99)
    for (idx <- 0 to len - 1) {
      assertEquals(solutions(idx),
        solutions(SelectionStrategy.getIndexForProbability(values(idx), probabilities)))
    }
  }
}
