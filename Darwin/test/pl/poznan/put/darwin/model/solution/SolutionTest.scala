package pl.poznan.put.darwin.model.solution

import pl.poznan.put.darwin.model.problem.{Problem, Parser}

class SolutionTest {
  import org.junit._, Assert._

  val trainsSoldiersNoIntervals: Problem = Parser.ProblemParser.parse(
    "var[0,200] x1;\n" +
    "var[0,200] x2;\n" +
    "max z: 3*x1 + 2*x2;\n" +
    "!dec: z;\n" +
    "Finishing: 2*x1 + x2 <= 100;\n" +
    "Carpentr: x1 + x2 <= 80;\n" +
    "Demand: x1 <= 40;\n" +
    "nonZero1: x1 >= 0;\n" +
    "nonZero2: x2 >= 0;\n"
  ).get

  @Test def solutionTest() {
    val s = Solution(trainsSoldiersNoIntervals, Map("x1" -> 10.0, "x2" -> 20.0))
    assertEquals(trainsSoldiersNoIntervals, s.problem)
    assertEquals(Map("x1" -> 10.0, "x2" -> 20.0), s.values)
    for (idx <- 1 to 50) {
      val r = Solution.random(trainsSoldiersNoIntervals)
      assertEquals(trainsSoldiersNoIntervals, r.problem)
      val x1 = r.values("x1")
      assertTrue(x1 >= 0)
      assertTrue(x1 <= 200)
      val x2 = r.values("x2")
      assertTrue(x2 >= 0)
      assertTrue(x2 <= 200)
    }
  }

}