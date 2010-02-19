package pl.poznan.put.darwin.experiment

import pl.poznan.put.darwin.model.Goal
import collection.mutable.HashMap


class SolutionResult {
  private var data: HashMap[Goal, List[Double]] = null

  def addResult(result: HashMap[Goal, Double]) {
    if (data == null) {
      data = new HashMap[Goal, List[Double]]
      result foreach {case (g, res) => data(g) = res :: Nil}
    } else {
      result foreach {case (g, res) => data(g) = res :: data(g)}
    }
  }
}