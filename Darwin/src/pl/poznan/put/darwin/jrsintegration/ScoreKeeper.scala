package pl.poznan.put.darwin.jrsintegration

import collection.mutable.HashMap
import pl.poznan.put.darwin.experiment.SolutionResult
import pl.poznan.put.darwin.model.Config.Solution
import pl.poznan.put.cs.idss.jrs.rules.{Rule, RulesContainer}
import pl.poznan.put.cs.idss.jrs.types.{Example, Field}
import pl.poznan.put.darwin.model.{Goal, Config}
import collection.jcl.MutableIterator.Wrapper


class ScoreKeeper(container: RulesContainer, var result: HashMap[Solution, SolutionResult]) {
  implicit def javaIteratorToScalaIterator[A](it: java.util.Iterator[A]) = new Wrapper(it)

  private var goals = result.values.collect(0).goals.collect
  private var weights: HashMap[Rule, Double] = _
  private var crowdingDistance: HashMap[Solution, Double] = _
  updateResult(result.toList)
  
  private def getPrimaryScore(s: Solution): Double = {
    var sum: Double = 0.0
    container.getRules(Rule.CERTAIN, Rule.AT_LEAST).iterator() foreach ((rule: Rule) => {
      val fields: List[Field] = SolutionConverter getFields result(s)
      val e: Example = new Example(fields.toArray[Field])
      if (rule covers e) sum += weights(rule)
    })
    sum
  }

  private def getSecondaryScore(s: Solution): Double = {
    crowdingDistance(s)
  }

  def updateResult(newResult: List[Tuple2[Solution, SolutionResult]]): HashMap[Solution, SolutionResult] = {
    result = new HashMap[Solution, SolutionResult]()

   newResult foreach {case (s: Solution, sr: SolutionResult) => {
     result(s) = sr
   }}

    weights = calculateWeights()
    crowdingDistance = calculateCrowding()
    result foreach {case (s: Solution, sr: SolutionResult) => {
      sr.primaryScore = getPrimaryScore(s)
      sr.secondaryScore = getSecondaryScore(s)
    }}
    result
  }

  private def calculateWeights(): HashMap[Rule, Double] = {
    val weights = new HashMap[Rule, Double]()
    val rules = container.getRules(Rule.CERTAIN, Rule.AT_LEAST)
    rules.iterator() foreach ((rule: Rule) => {
      var count = 0
      result.values foreach ((sr: SolutionResult) => {
        val fields: List[Field] = SolutionConverter getFields sr
        val e: Example = new Example(fields.toArray[Field])
        if (rule covers e) count += 1
      })
      weights(rule) = Math.pow(1 - Config.DELTA, count)
    })
    weights
  }

  private def calculateCrowding(): HashMap[Solution, Double] = {
    val crowdingDistance = new HashMap[Solution, Double]
    result.keys foreach ((s: Solution) => {
      crowdingDistance(s) = 0.0
    })
    goals foreach ((g: Goal) => {
      Config.PERCENTILES foreach (p => {
        var solutions: List[Solution] = result.keys.toList
        solutions.sort(crowdingDistanceLT(g, p))
        val s0 = solutions(0)
        crowdingDistance(s0) = Math.MAX_DOUBLE
        val sn = solutions.last
        crowdingDistance(sn) = Math.MAX_DOUBLE
        Iterator.range(1, solutions.length - 1) foreach ((idx) => {
          val sPrev = solutions(idx - 1)
          val s = solutions(idx)
          val sNext = solutions(idx + 1)
          val value = result(sNext).getPercentile(g, p) - result(sPrev).getPercentile(g, p)
          crowdingDistance(s) = incrementDistance(crowdingDistance(s), value)
        })
      })
    })
    crowdingDistance
  }

  private def crowdingDistanceLT(goal: Goal, p: Double)(self: Solution, other: Solution): Boolean = {
    val selfR = result(self)
    val otherR = result(other)
    if (selfR.getPercentile(goal, p) < otherR.getPercentile(goal, p)) true else false
  }

  private def incrementDistance(a: Double, b: Double): Double = {
    if (a == Math.MAX_DOUBLE || b == Math.MAX_DOUBLE) Math.MAX_DOUBLE
    else a + b
  }
}

object ScoreKeeper {
  def apply(container: RulesContainer, result: List[Tuple2[Solution, SolutionResult]]): ScoreKeeper = {
    var hashResult = new HashMap[Solution, SolutionResult]()

   result foreach {case (s: Solution, sr: SolutionResult) => {
     hashResult(s) = sr
   }}

    new ScoreKeeper(container, hashResult)
  }
}