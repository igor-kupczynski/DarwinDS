package pl.poznan.put.darwin.evolution
import collection.immutable.HashMap
import pl.poznan.put.darwin.model.problem.{VariableDef, Problem}
import pl.poznan.put.darwin.model.{Solution, Config}

/** 
* DarwinCrossOver
*
* Crossover operaror for darwin evolution framework
*
* @author Igor Kupczynski 
*/
object DarwinCrossOver  {

  def mate(a: Solution, b: Solution): Solution = {
    if (a.problem != b.problem) {
      throw new Exception("Solutions comes from different problems")
    }
    var c: Map[String, Double] = null
    while (c == null || !a.problem.isFeasible(c)) {
      val gamma: Double = Config.getRNG().nextDouble()
      c = new HashMap[String, Double]()
      a.problem.getVariables().map((v: VariableDef) => {
        c += (v.name -> (gamma * a.values(v.name) + (1-gamma) * b.values(v.name)))
      })
    }
    new Solution(a.problem, c)
  }
}
