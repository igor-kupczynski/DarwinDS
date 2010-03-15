package pl.poznan.put.darwin.model

import Config.Scenario
import java.util.Random
import collection.mutable.HashMap

abstract class ScenarioFactory {
  def generate(p: Problem): Scenario
}

object MonteCarloScenarioFactory extends ScenarioFactory {
  private val rng: Random = Config.getRNG()

  def generate(p: Problem): Scenario = {
    val result = new HashMap[String, Double]
    p.getIntervals() foreach ((i: Interval) => {
      result(i.name) = rng.nextDouble() * (i.upper - i.lower) + i.lower
    })
    result
  }
}