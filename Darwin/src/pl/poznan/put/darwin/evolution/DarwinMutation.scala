package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.problem.Problem
import pl.poznan.put.darwin.model.{Solution, Config}

/**
 * Mutation operator
 *
 * @author Igor Kupczynski
 */
object DarwinMutation {

  def mutate(s: Solution, generation: Int): Solution = {
    if (Config.getRNG().nextDouble() > Config.ETA * Math.pow(1 - Config.OMEGA, generation - 1))
      s.problem.randomNeighbour(s)
    else s
  }
}