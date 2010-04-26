package pl.poznan.put.darwin.model.solution
import org.junit.Assert._
import pl.poznan.put.darwin.model.problem.Goal
import org.junit.Test
import pl.poznan.put.darwin.evolution.ProblemRepository
import org.scalatest.junit.JUnitSuite

class MarkedSolutionTest extends JUnitSuite with ProblemRepository {

  @Test def markedSolutionTest() {
    val vals: Map[String, Double] = Map()
    val performances: Map[Goal, List[Double]] = Map()
    val s1 = new MarkedSolution(trainsSoldiersNoIntervalsSim, vals, performances, true)
    assertEquals(trainsSoldiersNoIntervalsSim, s1.sim)
    assertEquals(vals, s1.values)
    assertEquals(performances, s1.performances)
    assertEquals(true, s1.good)

    val baseS2 = new EvaluatedSolution(trainsSoldiersNoIntervalsSim, vals, performances)
    val s2 = MarkedSolution(baseS2, true)
    assertEquals(baseS2.sim, s2.sim)
    assertEquals(baseS2.values, s2.values)
    assertEquals(baseS2.performances, s2.performances)
    assertEquals(true, s2.good)


    val s3 = new MarkedSolution(trainsSoldiersNoIntervalsSim, vals, performances, false)
    assertEquals(trainsSoldiersNoIntervalsSim, s1.sim)
    assertEquals(vals, s3.values)
    assertEquals(performances, s3.performances)
    assertEquals(false, s3.good)

    val baseS4 = new EvaluatedSolution(trainsSoldiersNoIntervalsSim, vals, performances)
    val s4 = MarkedSolution(baseS2, false)
    assertEquals(baseS4.sim, s4.sim)
    assertEquals(baseS4.values, s4.values)
    assertEquals(baseS4.performances, s4.performances)
    assertEquals(false, s4.good)
  }

}
