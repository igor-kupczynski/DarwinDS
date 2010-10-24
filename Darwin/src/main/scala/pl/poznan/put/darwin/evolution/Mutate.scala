package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.solution.Solution
import pl.poznan.put.darwin.utils.RNG
import com.weiglewilczek.slf4s.Logging

/**
 * Mutate operator
 *
 * @author Igor Kupczynski
 */
object Mutate extends Logging {

  def apply(s: Solution, scenarios: List[Map[String, Double]],
            generation: Int): Solution = {
    logger info "Mutation started"
    if (RNG().nextDouble() > s.sim.config.ETA *
        math.pow(1 - s.sim.config.OMEGA, generation - 1))
      s.randomNeighbour(scenarios)
    else s
  }
}
