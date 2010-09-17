package pl.poznan.put.darwin.jrsintegration

import pl.poznan.put.darwin.model.solution.{MarkedSolution, EvaluatedSolution}
import pl.poznan.put.allrules.model.Column

object ObjectFactory {

  def apply(s: EvaluatedSolution): Map[String, Double] = {
    var r: Map[String, Double] = Map()
    for (g <- s.goals) {
      for (p <- s.sim.config.PERCENTILES) {
        r = r + ("%s_%s".format(g.name, p) -> s.getPercentile(g, p))
      }
    }
    r
  }

  def apply(s: MarkedSolution, cols: Map[String, Column[Any]]): Map[Column[Any], Any] = {
    var r: Map[Column[Any], Any] = Map()
    for (g <- s.goals) {
      for (p <- s.sim.config.PERCENTILES) {
        r = r + (cols("%s_%s".format(g.name, p)) -> s.getPercentile(g, p))
      }
    }
    r = r + (cols("Good") -> s.good)
    r
  }
}