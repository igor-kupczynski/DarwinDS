package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.Config.{Solution}
import pl.poznan.put.darwin.model.{Config, Problem}

/**
 * Mutation operator
 *
 * @author Igor Kupczynski
 */
object DarwinMutation {

  def mutate(p: Problem, s: Solution, generation: Int): Solution = {
    if (Config.getRNG().nextDouble() > Config.ETA * Math.pow(1 - Config.OMEGA, generation - 1))
      p.randomNeighbour(s)
    else s
  }
}