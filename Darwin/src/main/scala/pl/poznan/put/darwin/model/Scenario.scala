package pl.poznan.put.darwin.model

import java.util.Random
import problem.{Interval, Problem}

object Scenario extends  {
  private val rng: Random = Config.getRNG()

  def generate(p: Problem): Map[String, Double] = {
    var result: Map[String, Double] = Map()
    p.getIntervals() foreach ((i: Interval) => {
      result += (i.name -> (rng.nextDouble() * (i.upper - i.lower) + i.lower))
    })
    result
  }
}
