package pl.poznan.put.darwin.model.problem
import pl.poznan.put.darwin.evolution.DarwinCrossOver
import collection.mutable.HashMap

class DarwinCrossOverTest extends BaseEvolutionTestCase {

  import org.junit._, Assert._

  @Test def crossOverTest = {
    // It'll be quite hard to test crossover because is random. We'll
    // just create child few times and see if it is feasible and stays
    // between parents' values
    val a = new HashMap[String, Double]
    a("x1") = 0;
    a("x2") = 10;
    val b = new HashMap[String, Double]
    b("x1") = 10;
    b("x2") = 0;

    val xo = new DarwinCrossOver(trainsSoldiersNoIntervals)
    for (idx <- 1 to 10) {
      val c = xo.mate(a, b)
      assertTrue(c("x1") >= 0)
      assertTrue(c("x1") <= 10)
      assertTrue(c("x2") >= 0)
      assertTrue(c("x2") <= 10)
      assertTrue(trainsSoldiersNoIntervals.isFeasible(c))
    }
    
  }
}
