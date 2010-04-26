package pl.poznan.put.darwin.model.solution

import pl.poznan.put.darwin.model.problem.Goal
import pl.poznan.put.darwin.simulation.Simulation

/**
 * Solution marked by DM as good/not good.
 *
 * Solution is being evaluated before showing to DM.
 *
 * @author: Igor Kupczynski
 */
class MarkedSolution(sim: Simulation, values: Map[String, Double],
                     performances: Map[Goal, List[Double]], val good: Boolean)
        extends EvaluatedSolution(sim, values, performances) {

  override val name = "(M) Solution"

  override def equals(that: Any) = that match {
    case other: MarkedSolution => other.getClass == getClass &&
      other.sim == sim && other.values == values &&
      other.performances == performances && other.good == good
    case _ => false
  }

   override def hashCode: Int = super.hashCode * 41 + (if (good) 47 else -553)
}

/**
 * Companion object for creating MarkedSolution instances
 *
 * @author: Igor Kupczynski
 */
object MarkedSolution {
  def apply(s: EvaluatedSolution, good: Boolean): MarkedSolution = {
    new MarkedSolution(s.sim, s.values, s.performances, good)
  }
}
