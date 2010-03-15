package pl.poznan.put.darwin.evolution
import pl.poznan.put.darwin.model.Config.Solution
import pl.poznan.put.darwin.model.{Variable, Problem, Config}
import collection.mutable.HashMap

class DarwinCrossOver(problem: Problem)  {

  def mate(a: Solution, b: Solution): Solution = {
    val gamma: Double = Config.getRNG().nextDouble()
    val c = new HashMap[String, Double]()
    problem.getVariables().map((v: Variable) => {
      c(v.name) = gamma * a(v.name) + (1-gamma) * b(v.name)
    })
    c
  }
}