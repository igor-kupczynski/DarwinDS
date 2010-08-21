package pl.poznan.put.darwin.model.solution

import pl.poznan.put.darwin.simulation.Simulation
import pl.poznan.put.darwin.model.problem.{Problem, Evaluator, Goal}
import scala.collection.mutable.{HashMap => MutableHashMap}

/**
 * Solution evaluated on set of scenarios
 *
 * @param performance maps goal to sorted list of evaluation results
 *
 * @author: Igor Kupczynski
 */
class EvaluatedSolution(sim: Simulation, values: Map[String, Double],
                        protected[solution] val performances: Map[Goal, List[Double]])
        extends Solution(sim, values) {

  def utilityFunctionValue: Double = {
    var values: Map[String, Double] = Map()
    goals foreach ((g: Goal) => {
      values += (g.name -> getPercentile(g, 1))
      sim.problem.getQuantilesFor(g.name) foreach (q => {
        values += (("%s_%s" format (q.name, q.quantile)) -> getPercentile(g, 100 * q.quantile))
      })
    })
    Evaluator.evaluate(sim.problem.utilityFunction.expr, Map(), values)
  }


  override val name = "(E) Solution"

  override def equals(that: Any) = that match {
    case other: EvaluatedSolution => other.getClass == getClass &&
      other.sim == sim && other.values == values &&
      other.performances == performances
    case _ => false
  }

   override def hashCode: Int = super.hashCode * 41 + performances.hashCode
  
  /**
   * Returns value of given percentile on specified goal
   */
  def getPercentile(g: Goal, p: Double): Double = {
    // To low percentile (eg. 0%) will yield lowest percentile possible
    val floatIdx = performances(g).length * p / 100.0 - 1 // (-1 -> starting from 0
    val idx: Int = (math.round(floatIdx + 0.5) - 1).asInstanceOf[Int]
    val nonZeroIdx = if (idx < 0) 0 else idx
    if (sim.config.USE_AVG) avgUpToIdx(g, nonZeroIdx) else performances(g)(nonZeroIdx)
  }
  
  private def avgUpToIdx(g: Goal, idx: Int): Double = {
    val realIdx = if (idx > 0) idx else 0
    val toAvg = performances(g).take(realIdx + 1)
    val sum = toAvg.reduceLeft[Double]((a, b) => {a + b})
    sum / toAvg.size
  }
}


/**
 * Companion object for creating EvaluatedSolution instances
 *
 * @author: Igor Kupczynski
 */
object EvaluatedSolution {

  /**
   * Evaluates all the solutions given as arguments
   */
  def apply(solutions: List[Solution], scenarios: List[Map[String, Double]]):
      List[EvaluatedSolution] =
    solutions.map(s => apply(s, scenarios))
  
  /**
   * Creates one evaluated solution from given solution and scenarios
   */
  def apply(s: Solution, scenarios: List[Map[String, Double]]): EvaluatedSolution = {
    val problem = s.sim.problem

    val performances = new MutableHashMap[Goal, List[Double]]()
    for (g <- problem.goals) performances(g) = Nil

    scenarios foreach ( (scenario: Map[String, Double]) => {
      problem.goals foreach ((g: Goal) => {
        val result = Evaluator.evaluate(g.expr, scenario, s.values) 
        performances(g) = if (g.max) insertMax(result, performances(g))
                          else insertMin(result, performances(g))
      })
    })

    new EvaluatedSolution(s.sim, s.values, Map[Goal, List[Double]](performances.toList:_*))
  }


  private def insertMax(x: Double, xs: List[Double]): List[Double] = xs match {
      case List() => List(x)
      case y :: ys => if (x <= y) x :: xs else y :: insertMax(x, ys)
  }

  private def insertMin(x: Double, xs: List[Double]): List[Double] = xs match {
      case List() => List(x)
      case y :: ys => if (x >= y) x :: xs else y :: insertMin(x, ys)
  }

}
