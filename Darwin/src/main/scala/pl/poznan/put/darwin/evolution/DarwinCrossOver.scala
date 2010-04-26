package pl.poznan.put.darwin.evolution
import pl.poznan.put.darwin.model.problem.VariableDef
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.solution.{Solution, RankedSolution}
import pl.poznan.put.darwin.utils.RNG

/**
* DarwinCrossOver
*
* Crossover operaror for darwin evolution framework
*
* @author Igor Kupczynski 
*/
object CrossOver  {

  def apply(a: Solution, b: Solution): Solution = {
    var c: Map[String, Double] = null
    while (c == null || !(new Solution(a.sim, c)).isFeasible) {
      val gamma: Double = RNG.get().nextDouble()
      c = Map()
      a.sim.problem.getVariables().map((v: VariableDef) => {
        c += (v.name -> (gamma * a.values(v.name) + (1-gamma) * b.values(v.name)))
      })
    }

    Solution(a.sim, c)
  }
}
