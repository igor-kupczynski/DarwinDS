package pl.poznan.put.darwin.experiment

import pl.poznan.put.darwin.model.Goal
import collection.mutable.HashMap
import scala.Iterator.empty


class SolutionResult {
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
      result foreach {case (g: Goal, res) => data(g) = if (g.isMax) insertMax(res, data(g)) else insertMin(res, data(g))}
    }
  }

  def getPercentile(g: Goal, p: Double): Double = {
    val floatIdx = data(g).length * p / 100.0
    val idx: Int = (Math.round(floatIdx + 0.5) - 1).asInstanceOf[Int]
    data(g)(idx)
  }

  def goals(): Iterator[Goal] = {
    if (data != null) data.keys else empty
  }

  def autoValue: Double = {
    var value: Double = 0.0;
    goals() foreach ((g: Goal) => {
      var tmp = getPercentile(g, 1.0)
      tmp += 3 * getPercentile(g, 25.0)
      tmp += 2 * getPercentile(g, 50.0)
      value += (if (g.isMax) tmp else (-1) * tmp)
    })
    value
  }
}