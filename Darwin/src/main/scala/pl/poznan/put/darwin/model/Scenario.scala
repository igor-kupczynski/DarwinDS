package pl.poznan.put.darwin.model

import java.util.Random
import problem.{Interval, Problem}
import pl.poznan.put.darwin.utils.RNG

object Scenario extends  {
  private val rng: RNG = RNG()

  def generate(p: Problem): Map[String, Double] = {
    var result: Map[String, Double] = Map()
    p.getIntervals() foreach ((i: Interval) => {
      result += (i.name -> (rng.nextDouble() * (i.upper - i.lower) + i.lower))
    })
    result
  }
}
