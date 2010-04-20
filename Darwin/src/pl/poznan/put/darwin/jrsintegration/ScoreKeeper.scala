package pl.poznan.put.darwin.jrsintegration

import collection.immutable.HashMap
import pl.poznan.put.cs.idss.jrs.rules.{Rule, RulesContainer}
import pl.poznan.put.cs.idss.jrs.types.{Example, Field}
import collection.jcl.MutableIterator.Wrapper
import pl.poznan.put.darwin.model.problem.Goal
import pl.poznan.put.darwin.model.{Solution, Config}


class ScoreKeeper(container: RulesContainer, var result: List[Solution]) {
  implicit def javaIteratorToScalaIterator[A](it: java.util.Iterator[A]) = new Wrapper(it)

  private var goals = result(0).goals
  private var weights: Map[Rule, Double] = _
  private var crowdingDistance: Map[Solution, Double] = _
  updateResult(result.toList)
  
  private def getPrimaryScore(s: Solution): Double = {
    var sum: Double = 0.0
    container.getRules(Rule.CERTAIN, Rule.AT_LEAST).iterator() foreach ((rule: Rule) => {
      val fields: List[Field] = SolutionConverter getFields s
      val e: Example = new Example(fields.toArray[Field])
      if (rule covers e) sum += weights(rule)
    })
    sum
  }

  private def getSecondaryScore(s: Solution): Double = {
    crowdingDistance(s)
  }

  def updateResult(newResult: List[Solution]) {
    result = newResult
    weights = calculateWeights()
    crowdingDistance = calculateCrowding()
    result foreach ((s: Solution) => {
      s.setPrimaryScore(this.getPrimaryScore(s))
      s.setSecondaryScore(this.getSecondaryScore(s))
    })
    result = setFitness()
    for (idx <- 0 to result.length - 1) {
      result(idx).setFitness(idx)
    }
  }

  private def setFitness(): List[Solution] = {
    def lessThat(a: Solution, b: Solution): Boolean = {
      if (a.getPrimaryScore > b.getPrimaryScore ||
              (a.getPrimaryScore == b.getPrimaryScore && a.getSecondaryScore > b.getSecondaryScore))
        true
      else
        false
    }
    result.sort(lessThat)
  }

  private def calculateWeights(): Map[Rule, Double] = {
    var weights: Map[Rule, Double] = new HashMap[Rule, Double]()
    val rules = container.getRules(Rule.CERTAIN, Rule.AT_LEAST)
    rules.iterator() foreach ((rule: Rule) => {
      var count = 0
      result foreach ((s: Solution) => {
        val fields: List[Field] = SolutionConverter getFields s
        val e: Example = new Example(fields.toArray[Field])
        if (rule covers e) count += 1
      })
      weights += (rule -> Math.pow(1 - Config.DELTA, count))
    })
    weights
  }

  private def calculateCrowding(): Map[Solution, Double] = {
    var crowdingDistance: Map[Solution, Double] = new HashMap[Solution, Double]
    result foreach ((s: Solution) => {
      crowdingDistance += (s -> 0.0)
    })
    goals foreach ((g: Goal) => {
      Config.PERCENTILES foreach (p => {
        var solutions: List[Solution] = result
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

  private def crowdingDistanceLT(goal: Goal, p: Double)(self: Solution, other: Solution): Boolean = {
    if (self.getPercentile(goal, p) < other.getPercentile(goal, p)) true else false
  }

  private def incrementDistance(a: Double, b: Double): Double = {
    if (a == Math.MAX_DOUBLE || b == Math.MAX_DOUBLE) Math.MAX_DOUBLE
    else a + b
  }
}