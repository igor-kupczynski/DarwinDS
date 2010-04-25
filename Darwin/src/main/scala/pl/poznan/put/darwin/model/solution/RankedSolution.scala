package pl.poznan.put.darwin.model.solution

import pl.poznan.put.darwin.model.problem.{Problem, Goal}
import pl.poznan.put.cs.idss.jrs.rules.{Rule}
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.jrsintegration.{DarwinRulesContainer, ExampleFactory}

/**
 * Solution evaluated on set of scenarios and with primary and secondary score calculated.
 *
 * @author: Igor Kupczynski
 */
class RankedSolution(problem: Problem, values: Map[String, Double],
                        performances: Map[Goal, List[Double]],
                        val primaryScore: Double, val secondaryScore: Double, val rank: Int)
        extends EvaluatedSolution(problem, values, performances) {
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
   * Create gang of RankedSolutions from pack of EvaluatedSolutions. Solutions are sorted at the end
   */
  def apply(solutions: List[EvaluatedSolution], rulesContainer: DarwinRulesContainer): List[RankedSolution] = {
    val primaryScores: Map[EvaluatedSolution, Double] = calculatePrimary(rulesContainer.rules, solutions)
    val crowdingDistances = calculateCrowding(solutions)

    def fitnessLT(self: EvaluatedSolution, other: EvaluatedSolution): Boolean =
      if (primaryScores(self) > primaryScores (other) ||
              (primaryScores(self) == primaryScores (other) && crowdingDistances(self) > crowdingDistances(other)))
        true
      else
        false

    val sortedSolutions = solutions.sortWith(fitnessLT)
    var count = 0
    (0 to (sortedSolutions.length - 1)).map(idx => {
      val s = sortedSolutions(idx)
      new RankedSolution(s.problem, s.values, s.performances, primaryScores(s), crowdingDistances(s), idx+1)
    }).toList
  }


  // Part for calculating primary score
  private def calculatePrimary(rules: List[(Rule, Double)],
                               solutions: List[EvaluatedSolution]):
        Map[EvaluatedSolution, Double] = {

    var scores: Map[EvaluatedSolution, Double] = Map()
    solutions foreach ((s: EvaluatedSolution) => {
      val sum = rules.foldLeft[Double](0.0)(
        (sum: Double, pair: Tuple2[Rule, Double]) => {
          val r = pair._1
          val w = pair._2
          if (r covers ExampleFactory(s))
            sum + w
          else
            sum
        }
      )
      scores += (s -> sum)
    })
    scores
  }


  // Part for calculate crowding distance. So secondary score 

  private def crowdingDistanceLT(goal: Goal, p: Double)(self: EvaluatedSolution, other: EvaluatedSolution): Boolean = {
    if (self.getPercentile(goal, p) < other.getPercentile(goal, p)) true else false
  }

  private def incrementDistance(a: Double, b: Double): Double = {
    if (a == Double.MaxValue || b == Double.MaxValue)
      Double.MaxValue
    else a + b
  }

  private def calculateCrowding(solutions: List[EvaluatedSolution]): Map[EvaluatedSolution, Double] = {
    var crowdingDistance: Map[EvaluatedSolution, Double] = Map()
    solutions foreach ((s: EvaluatedSolution) => {
      crowdingDistance += (s -> 0.0)
    })
    val goals = solutions(0).goals
    goals foreach ((g: Goal) => {
      Config.PERCENTILES foreach (p => {
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