package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.solution.Solution

/**
 * Mutate operator
 *
 * @author Igor Kupczynski
 */
object Mutate {

  def apply(s: Solution, generation: Int): Solution =
    if (Config.getRNG().nextDouble() > Config.ETA * Math.pow(1 - Config.OMEGA, generation - 1))
      s.problem.randomNeighbour(s)
    else s
}