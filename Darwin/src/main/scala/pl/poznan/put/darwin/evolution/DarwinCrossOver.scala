package pl.poznan.put.darwin.evolution
import pl.poznan.put.darwin.model.problem.{BinaryConstraint,
                                           IntegerConstraint,
                                           VariableDef}
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.solution.{Solution, RankedSolution}
import pl.poznan.put.darwin.utils.RNG
import com.weiglewilczek.slf4s.Logging

/**
* CrossOver
*
* Crossover operaror for darwin evolution framework
*
* @author Igor Kupczynski 
*/
object CrossOver extends Logging {

  def apply(a: Solution, b: Solution, scenarios: List[Map[String, Double]]):
      Solution = {

    logger info "Crossing the parents over"

    var c: Map[String, Double] = null
    while (c == null ||
           !(new Solution(a.sim, c)).isFeasibleOnScenarios(scenarios)) {
      val gamma: Double = RNG().nextDouble()
      c = Map()
      a.sim.problem.getVariables().foreach({
        case VariableDef(name, min, max, BinaryConstraint) =>
          c += (name -> (if (gamma < 0.5) a.values(name) else b.values(name)))
        case VariableDef(name, min, max, IntegerConstraint) =>
          c += (name -> math.round(gamma * a.values(name) + (1-gamma) * b.values(name)))
        case VariableDef(name, min, max, null) =>
          c += (name -> (gamma * a.values(name) + (1-gamma) * b.values(name)))
      })
    }
    Solution(a.sim, c)
  }
}
