package pl.poznan.put.darwin.model.solution

import pl.poznan.put.darwin.jrsintegration.DarwinRulesContainer
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.Goal
import pl.poznan.put.darwin.simulation.Simulation

/**
 * Solution evaluated on set of scenarios and with primary and secondary score calculated.
 *
 * @author: Igor Kupczynski
 */
class RankedSolution(sim: Simulation, values: Map[String, Double],
                        performances: Map[Goal, List[Double]],
                        val primaryScore: Double, val secondaryScore: Double, val rank: Int)
        extends EvaluatedSolution(sim, values, performances) {

  override val name = "(R) Solution"

  override def equals(that: Any) = that match {
    case other: RankedSolution => other.getClass == getClass &&
      other.sim == sim && other.values == values &&
      other.performances == performances &&
      other.primaryScore == primaryScore &&
      other.secondaryScore == secondaryScore &&
      other.rank == rank
    case _ => false
  }

   override def hashCode: Int = super.hashCode * 41 +
                                (1341 * primaryScore).asInstanceOf[Int] +
                                (2331 * secondaryScore).asInstanceOf[Int] +
                                17 * rank
}


/**
 * Companion object for creating RankedSolution instances.
 *
 * In fact it is a workhorse for RankedSolutions because they are only
 * containing precomputed values. And the values are being precomputed here.
 *
 * One cannot create single instance fo RankedSolution. You need a band of
 * EvaluatedSolutions to create a band of RankedSolutions. N-in, N-out.
 *
 * @author: Igor Kupczynski
 */
object RankedSolution {

  /**
   * Create gang of RankedSolutions from pack of EvaluatedSolutions.
   * Solutions are sorted at the end
   */
  def apply(solutions: List[EvaluatedSolution],
            rulesContainer: DarwinRulesContainer):
      List[RankedSolution] = {
    val primaryScores: Map[EvaluatedSolution, Double] =
      calculatePrimary(rulesContainer, solutions)
    val crowdingDistances = calculateCrowding(solutions)

    def fitnessLT(self: EvaluatedSolution,
                  other: EvaluatedSolution): Boolean =
      if (primaryScores(self) > primaryScores (other) ||
              (primaryScores(self) == primaryScores (other) &&
               crowdingDistances(self) > crowdingDistances(other)))
        true
      else
        false

    val sortedSolutions = solutions.sortWith(fitnessLT)
    var count = 0
    (0 to (sortedSolutions.length - 1)).map(idx => {
      val s = sortedSolutions(idx)
      new RankedSolution(s.sim, s.values, s.performances,
                         primaryScores(s), crowdingDistances(s), idx+1)
    }).toList
  }


  // Part for calculating primary score
  private def calculatePrimary(container: DarwinRulesContainer,
                               solutions: List[EvaluatedSolution]):
        Map[EvaluatedSolution, Double] = {
    var scores: Map[EvaluatedSolution, Double] = Map()
    solutions foreach ((s: EvaluatedSolution) =>
      scores += (s -> container.getScore(s)))
    scores
  }


  // Part for calculate crowding distance. So secondary score 

  private def crowdingDistanceLT(goal: Goal, p: Double)(
      self: EvaluatedSolution, other: EvaluatedSolution): Boolean = {
    if (self.getPercentile(goal, p) < other.getPercentile(goal, p)) true else false
  }

  private def incrementDistance(a: Double, b: Double): Double = {
    if (a == Double.MaxValue || b == Double.MaxValue)
      Double.MaxValue
    else a + b
  }

  private def calculateCrowding(solutions: List[EvaluatedSolution]):
      Map[EvaluatedSolution, Double] = {
    var crowdingDistance: Map[EvaluatedSolution, Double] = Map()
    solutions foreach ((s: EvaluatedSolution) => {
      crowdingDistance += (s -> 0.0)
    })
    val goals = solutions(0).goals
    goals foreach ((g: Goal) => {
      (new Config()).PERCENTILES foreach (p => {
        val sorted = solutions.sortWith(crowdingDistanceLT(g, p))
        val s0 = sorted(0)
        crowdingDistance += (s0 -> Double.MaxValue)
        val sn = sorted.last
        crowdingDistance += (sn -> Double.MaxValue)
        for (idx <- 1 to sorted.length - 2) {
          val sPrev = sorted(idx - 1)
          val s = sorted(idx)
          val sNext = sorted(idx + 1)
          val value = sNext.getPercentile(g, p) - sPrev.getPercentile(g, p)
          crowdingDistance += (s -> incrementDistance(crowdingDistance(s), value))
        }
      })
    })
    crowdingDistance
  }

}
