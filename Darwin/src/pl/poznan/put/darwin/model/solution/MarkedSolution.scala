package pl.poznan.put.darwin.model.solution

import pl.poznan.put.darwin.model.Config
import scala.collection.mutable.{HashMap => MutableHashMap}
import pl.poznan.put.darwin.model.problem.{Problem, Goal}

/**
 * Solution marked by DM as good/not good.
 *
 * Solution is being evaluated before showing to DM.
 *
 * @author: Igor Kupczynski
 */
class MarkedSolution(problem: Problem, values: Map[String, Double],
                     performances: Map[Goal, List[Double]], val good: Boolean)
        extends EvaluatedSolution(problem, values, performances) {
}


/**
 * Companion object for creating MarkedSolution instances
 *
 * @author: Igor Kupczynski
 */
object MarkedSolution {
  def apply(s: EvaluatedSolution, good: Boolean): MarkedSolution = {
    new MarkedSolution(s.problem, s.values, s.performances, good)
  }
}