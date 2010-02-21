package pl.poznan.put.darwin.experiment

import pl.poznan.put.darwin.model.Goal
import collection.mutable.HashMap


class SolutionResult {
  private var data: HashMap[Goal, List[Double]] = null
  private var percentiles: HashMap[(Goal, Int), Double] = null

  var isGood: Boolean = _

  def addResult(result: HashMap[Goal, Double]) {

  def insert(x: Double, xs: List[Double]): List[Double] = xs match {
    case List() => List(x)
    case y :: ys => if (x <= y) x :: xs else y :: insert(x, ys)
  }


    if (data == null) {
      data = new HashMap[Goal, List[Double]]
      result foreach {case (g, res) => data(g) = res :: Nil}
    } else {
      result foreach {case (g, res) => data(g) = insert(res, data(g))}
    }
  }

  def getPercentil(g: Goal, p: Double): Double = {
    val idx: Int = (Math.round(data(g).length * p / 100.0 + 0.5) - 1).asInstanceOf[Int]
    data(g)(idx)
  }
}