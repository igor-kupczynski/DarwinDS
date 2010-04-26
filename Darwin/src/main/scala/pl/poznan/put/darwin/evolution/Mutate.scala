package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.solution.Solution
import pl.poznan.put.darwin.utils.RNG

/**
 * Mutate operator
 *
 * @author Igor Kupczynski
 */
object Mutate {

  def apply(s: Solution, generation: Int): Solution =
    if (RNG.get().nextDouble() > (new Config()).ETA * math.pow(1 - (new Config()).OMEGA, generation - 1))
      s.randomNeighbour
    else s
}
