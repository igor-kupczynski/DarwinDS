package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.Solution

class DarwinMutationTest extends BaseEvolutionTestCase {

  import org.junit._, Assert._

  @Test def mutationTest = {
    // It'll be quite hard to test mutation because it is random. We'll
    // just create a mutant few times and see if it is feasible
    val a = Map("x1" -> 10.0, "x2" -> 10.0)

    for (idx <- 1 to 100) {
      val c = DarwinMutation.mutate(new Solution(trainsSoldiersNoIntervals, a), 1)
      assertTrue(trainsSoldiersNoIntervals.isFeasible(c))
      assertEquals(c.values.keySet.size, 2)
      assertTrue(c.values.contains("x1"))
      assertTrue(c.values.contains("x2"))
    }
    
  }
}
