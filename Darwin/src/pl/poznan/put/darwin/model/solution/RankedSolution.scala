package pl.poznan.put.darwin.model.solution

import pl.poznan.put.darwin.model.problem.{Problem, Goal}
import pl.poznan.put.cs.idss.jrs.rules.{Rule, RulesContainer}
import pl.poznan.put.darwin.model.Config
import collection.immutable.HashMap
import pl.poznan.put.darwin.jrsintegration.ExampleFactory

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
  def apply(solutions: List[EvaluatedSolution], rulesContainer: RulesContainer): List[RankedSolution] = {
    val rules: List[Rule] = rulesContainer.getRules(Rule.CERTAIN, Rule.AT_LEAST).toArray(new Array[Rule](0)).toList
    val primaryScores: Map[EvaluatedSolution, Double] = calculatePrimary(rules, solutions)
    val crowdingDistances = calculateCrowding(solutions)

    def fitnessLT(self: EvaluatedSolution, other: EvaluatedSolution): Boolean =
      if (primaryScores(self) > primaryScores (other) ||
              (primaryScores(self) == primaryScores (other) && crowdingDistances(self) > crowdingDistances(other)))
        true
      else
        false

    val sortedSolutions = solutions.sort(fitnessLT)
    var count = 0
    (0 to (sortedSolutions.length - 1)).map[RankedSolution](idx => {
      val s = sortedSolutions(idx)
      new RankedSolution(s.problem, s.values, s.performances, primaryScores(s), crowdingDistances(s), idx+1)
    }).toList
  }


  // Part for calculating primary score

  private def calculateWeights(rules: List[Rule], solutions: List[EvaluatedSolution]): Map[Rule, Double] = {
    var weights: Map[Rule, Double] = new HashMap[Rule, Double]()
    rules foreach ((rule: Rule) => {
      val count = solutions.filter(s => rule covers ExampleFactory(s)).length
      weights += (rule -> Math.pow(1 - Config.DELTA, count))
    })
    weights
  }

  private def calculatePrimary(rules: List[Rule], solutions: List[EvaluatedSolution]): Map[EvaluatedSolution, Double] = {
    val weights = calculateWeights(rules, solutions)
    var scores: Map[EvaluatedSolution, Double] = new HashMap[EvaluatedSolution, Double]()
    solutions foreach ((s: EvaluatedSolution) => {
      val sum = rules.foldLeft[Double](0.0)(
        (sum: Double, r: Rule) =>
          if (r covers ExampleFactory(s))
            sum + weights(r)
          else
            sum
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
    if (a == Math.MAX_DOUBLE || b == Math.MAX_DOUBLE)
      Math.MAX_DOUBLE
    else a + b
  }

  private def calculateCrowding(solutions: List[EvaluatedSolution]): Map[EvaluatedSolution, Double] = {
    var crowdingDistance: Map[EvaluatedSolution, Double] = new HashMap[EvaluatedSolution, Double]
    solutions foreach ((s: EvaluatedSolution) => {
      crowdingDistance += (s -> 0.0)
    })
    val goals = solutions(0).goals
    goals foreach ((g: Goal) => {
      Config.PERCENTILES foreach (p => {
        solutions.sort(crowdingDistanceLT(g, p))
        val s0 = solutions(0)
        crowdingDistance += (s0 -> Math.MAX_DOUBLE)
        val sn = solutions.last
        crowdingDistance += (sn -> Math.MAX_DOUBLE)
        for (idx <- 1 to solutions.length - 2) {
          val sPrev = solutions(idx - 1)
          val s = solutions(idx)
          val sNext = solutions(idx + 1)
          val value = sNext.getPercentile(g, p) - sPrev.getPercentile(g, p)
          crowdingDistance += (s -> incrementDistance(crowdingDistance(s), value))
        }
      })
    })
    crowdingDistance
  }

}