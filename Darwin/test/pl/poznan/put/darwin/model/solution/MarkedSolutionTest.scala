package pl.poznan.put.darwin.model.solution

import pl.poznan.put.darwin.model.problem.Goal

class MarkedSolutionTest extends EvaluatedSolutionTest {
  import org.junit._, Assert._


  @Test def markedSolutionTest() {
    val vals: Map[String, Double] = Map()
    val performances: Map[Goal, List[Double]] = Map()
    val s1 = new MarkedSolution(trainsSoldiersNoIntervals, vals, performances, true)
    assertEquals(trainsSoldiersNoIntervals, s1.problem)
    assertEquals(vals, s1.values)
    assertEquals(performances, s1.performances)
    assertEquals(true, s1.good)

    val baseS2 = new EvaluatedSolution(trainsSoldiersNoIntervals, vals, performances)
    val s2 = MarkedSolution(baseS2, true)
    assertEquals(baseS2.problem, s2.problem)
    assertEquals(baseS2.values, s2.values)
    assertEquals(baseS2.performances, s2.performances)
    assertEquals(true, s2.good)


    val s3 = new MarkedSolution(trainsSoldiersNoIntervals, vals, performances, false)
    assertEquals(trainsSoldiersNoIntervals, s1.problem)
    assertEquals(vals, s3.values)
    assertEquals(performances, s3.performances)
    assertEquals(false, s3.good)

    val baseS4 = new EvaluatedSolution(trainsSoldiersNoIntervals, vals, performances)
    val s4 = MarkedSolution(baseS2, false)
    assertEquals(baseS4.problem, s4.problem)
    assertEquals(baseS4.values, s4.values)
    assertEquals(baseS4.performances, s4.performances)
    assertEquals(false, s4.good)
  }

}