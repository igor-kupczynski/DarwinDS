package pl.poznan.put.darwin.evolution
import pl.poznan.put.darwin.model.Config.Solution
import pl.poznan.put.darwin.model.Config
import collection.mutable.HashMap
import pl.poznan.put.darwin.model.problem.{VariableDef, Problem}

class DarwinCrossOver(problem: Problem)  {

  def mate(a: Solution, b: Solution): Solution = {
    var c: HashMap[String, Double] = null
    while (c == null || !problem.isFeasible(c)) {
      val gamma: Double = Config.getRNG().nextDouble()
      c = new HashMap[String, Double]()
      problem.getVariables().map((v: VariableDef) => {
        c(v.name) = gamma * a(v.name) + (1-gamma) * b(v.name)
      })
    }
    c
  }
}