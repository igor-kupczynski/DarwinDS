package pl.poznan.put.darwin.evolution

import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import pl.poznan.put.darwin.ProblemRepository
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.solution.RankedSolution

class SelectionStrategyTest extends JUnitSuite with ProblemRepository {

  @Test def probabilitiesTest = {
    val solutions: List[RankedSolution] =
      new RankedSolution(trainsSoldiersNoIntervalsSim, Map(), Map(), 0.0, 0.0, 1) ::
      new RankedSolution(trainsSoldiersNoIntervalsSim, Map(), Map(), 0.0, 0.0, 2) ::
      new RankedSolution(trainsSoldiersNoIntervalsSim, Map(), Map(), 0.0, 0.0, 3) ::
      new RankedSolution(trainsSoldiersNoIntervalsSim, Map(), Map(), 0.0, 0.0, 4) ::
      new RankedSolution(trainsSoldiersNoIntervalsSim, Map(), Map(), 0.0, 0.0, 5) ::
      Nil

    val len = solutions.length

    val probabilities = SelectionStrategy.computeProbabilities(solutions)
    assertEquals(len, probabilities.length)

    val sim = solutions(0).sim
    var acc = 0.0
    for (idx <- 1 to len) {
      val delta = math.pow( (1.0 + len - idx) / len, sim.config.GAMMA) -
          math.pow((0.0 + len - idx) / len, sim.config.GAMMA)
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
