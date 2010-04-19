package pl.poznan.put.darwin.evolution

import collection.mutable.HashMap

class DarwinMutationTest extends BaseEvolutionTestCase {

  import org.junit._, Assert._

  @Test def mutationTest = {
    // It'll be quite hard to test mutation because it is random. We'll
    // just create a mutant few times and see if it is feasible
    val a = new HashMap[String, Double]
    a("x1") = 10;
    a("x2") = 10;

    for (idx <- 1 to 100) {
      val c = DarwinMutation.mutate(trainsSoldiersNoIntervals, a, 1)
      assertTrue(trainsSoldiersNoIntervals.isFeasible(c))
      assertTrue(c("x1") != null)
      assertTrue(c("x2") != null)
    }
    
  }
}
