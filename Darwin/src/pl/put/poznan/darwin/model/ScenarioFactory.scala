package pl.put.poznan.darwin.model

import Config.Scenario
import scala.util.Random

abstract class ScenarioFactory {
  def generate(p: Problem): Scenario
}

object MonteCarloScenarioFactory extends ScenarioFactory {

  private val rng: Random = Config.getRNG()

  def generate(p: Problem): Scenario = {
    var result: Map[String, Double] = Map()
    p.getIntervals() foreach ((i: Interval) => {
      result(i.name) = rng.nextDouble() * (i.upper - i.lower) + i.lower
    })
    result
  }
}