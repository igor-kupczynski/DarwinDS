package pl.poznan.put.darwin.model

import collection.immutable.HashMap
import collection.mutable
import problem.{Evaluator, Goal, Problem}

object SolutionState extends Enumeration {
  type SolutionState = Value

  val CREATED = Value("CREATED")
  val EVALUATING = Value("EVALUATING")
  val EVALUATED = Value("EVALUATED")
  val MARKED = Value("MARKED")
  val SCORED = Value("SCORED")
  val FITNESS = Value("FITNESS")
}

import SolutionState._

/**
 * Class representing solution. Acts as state machine folwing processes of solution evaluation.
 *
 * @author: Igor Kupczynski
 */
class Solution(val problem: Problem, val values: Map[String, Double]) {

  private var state: SolutionState = CREATED
  private var data: mutable.Map[Goal, List[Double]] = _
  private var good: Boolean = _
  private var primaryScore: Double = Math.MIN_DOUBLE
  private var secondaryScore: Double = Math.MIN_DOUBLE
  private var fitness: Int = _

  def getFitness(): Int = {
    if (state < FITNESS) {
      throw new Exception("No fitness. Set one first")
    }
    fitness
  }

  def setFitness(x: Int) {
    if (state != SCORED) {
      throw new Exception("Can not set fitness in state %s. It is possible in SCORED" format state)
    }
    fitness = x
    state = FITNESS
  }

  def getPrimaryScore(): Double = {
    if (state != SCORED) {
      throw new Exception("No primary score. Score solution first")
    }
    primaryScore
  }

  def setPrimaryScore(x: Double) {
    if (state != EVALUATED) {
      throw new Exception("Can not set primary score in state %s. It is possible in EVALUATED" format state)
    }
    if (primaryScore != Math.MIN_DOUBLE) {
      throw new Exception("Primary score has been set before")
    }
    primaryScore = x
    if (secondaryScore != Math.MIN_DOUBLE) {
      state = SCORED
    }
  }

  def getSecondaryScore(): Double = {
    if (state != SCORED) {
      throw new Exception("No secondary score. Score solution first")
    }
    secondaryScore
  }

    def setSecondaryScore(x: Double) {
    if (state != EVALUATED) {
      throw new Exception("Can not set secondary score in state %s. It is possible in EVALUATED" format state)
    }
    if (secondaryScore != Math.MIN_DOUBLE) {
      throw new Exception("Secondary score has been set before")
    }
    secondaryScore = x
    if (primaryScore != Math.MIN_DOUBLE) {
      state = SCORED
    }
  }

  def markGood() {
    if (state != EVALUATED) {
      throw new Exception("Can not mark solution. Solution in state %s instead of EVALUATED" format state)
    }
    good = true
  }

  def markNotGood() {
    if (state != EVALUATED) {
      throw new Exception("Can mark solution. Solution in state %s instead of EVALUATED" format state)
    }
    good = false
  }

  def isGood(): Boolean = {
    if (state >= MARKED) {
      throw new Exception("Mark solution first")
    }
    good
  }

  def evaluateScenario(scenario: Map[String, Double]) {
    var result: Map[Goal, Double] = new HashMap[Goal, Double]
    problem.goals foreach ((g: Goal) => {
      result += (g -> Evaluator.evaluate(g.expr, scenario, values))
    })
    addResult(result)
  }

  private def addResult(result: Map[Goal, Double]) {
    if (state == CREATED) {
      state = EVALUATING
      data = new mutable.HashMap[Goal, List[Double]]
      problem.goals foreach ((g: Goal) => data(g) = Nil)
    }

    if (state != EVALUATING) {
      throw new Exception("Can not add new result. Solution in state %s instead of EVALUATING" format state)
    }
    
    def insertMax(x: Double, xs: List[Double]): List[Double] = xs match {
      case List() => List(x)
      case y :: ys => if (x <= y) x :: xs else y :: insertMax(x, ys)
    }

    def insertMin(x: Double, xs: List[Double]): List[Double] = xs match {
      case List() => List(x)
      case y :: ys => if (x >= y) x :: xs else y :: insertMin(x, ys)
    }

    result foreach {case (g: Goal, res) =>
      data(g) = if (g.max) insertMax(res, data(g)) else insertMin(res, data(g))}
  }


  def finishEvaluation() {
    if (state != EVALUATING) {
      throw new Exception("Can not finish evaluation. Solution in state %s instead of EVALUATING" format state)
    }

    state = EVALUATED
  }

  def goals: List[Goal] = {
    problem.goals
  }


  def getPercentile(g: Goal, p: Double): Double = {
    if (state < EVALUATED) {
      throw new Exception("Finish evaluation to get percentiles first")
    }
    val floatIdx = data(g).length * p / 100.0
    val idx: Int = (Math.round(floatIdx + 0.5) - 1).asInstanceOf[Int]
    if (Config.USE_AVG) avgUpToIdx(g, idx) else data(g)(idx)
  }


  def utilityFunctionValue: Double = {
    if (state < EVALUATED) {
      throw new Exception("Finish evaluation to get utilityFunctionValue first")
    }
    var result: Map[String, Double] = new HashMap[String, Double]
    goals foreach ((g: Goal) => {
      //TODO: generalize to interval case
      result += (g.name -> getPercentile(g, 0))
    })
    return problem.utilityValue(new Solution(problem, result))
  }

  private def avgUpToIdx(g: Goal, idx: Int): Double = {
    val toAvg = data(g).take(idx + 1)
    val sum = toAvg.reduceLeft[Double]((a, b) => {a + b})
    sum / toAvg.size
  }
}

