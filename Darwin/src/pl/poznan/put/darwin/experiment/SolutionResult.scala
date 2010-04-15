package pl.poznan.put.darwin.experiment

import collection.mutable.HashMap
import scala.Iterator.empty
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.{Evaluator, Problem, Goal}

class SolutionResult(problem: Problem) {
  private var data: HashMap[Goal, List[Double]] = null
  private var percentiles: HashMap[(Goal, Int), Double] = null



  var isGood: Boolean = _

  var primaryScore: Double = _
  var secondaryScore: Double = _
  var fitness: Double = _

  def addResult(result: HashMap[Goal, Double]) {

    def insertMax(x: Double, xs: List[Double]): List[Double] = xs match {
      case List() => List(x)
      case y :: ys => if (x <= y) x :: xs else y :: insertMax(x, ys)
    }

    def insertMin(x: Double, xs: List[Double]): List[Double] = xs match {
      case List() => List(x)
      case y :: ys => if (x >= y) x :: xs else y :: insertMin(x, ys)
    }

    if (data == null) {
      data = new HashMap[Goal, List[Double]]
      result foreach {case (g, res) => data(g) = res :: Nil}
    } else {
      result foreach {case (g: Goal, res) => data(g) = if (g.max) insertMax(res, data(g)) else insertMin(res, data(g))}
    }
  }

  def getPercentile(g: Goal, p: Double): Double = {
    val floatIdx = data(g).length * p / 100.0
    val idx: Int = (Math.round(floatIdx + 0.5) - 1).asInstanceOf[Int]
    if (Config.USE_AVG) avgUpToIdx(g, idx) else data(g)(idx)
  }

  def goals(): Iterator[Goal] = {
    if (data != null) data.keys else empty
  }

  def utilityFunctionValue: Double = {
    val result = new HashMap[String, Double]
    goals foreach ((g: Goal) => {
      //TODO: generalize to interval case
      result(g.name) = getPercentile(g, 0)
    })
    return problem.utilityValue(result)
  }

  private def avgUpToIdx(g: Goal, idx: Int): Double = {
    val toAvg = data(g).take(idx + 1)
    val sum = toAvg.reduceLeft[Double]((a, b) => {a + b})
    sum / toAvg.size
  }
}