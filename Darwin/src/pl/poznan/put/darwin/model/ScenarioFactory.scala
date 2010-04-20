package pl.poznan.put.darwin.model

import Config.Scenario
import java.util.Random
import collection.immutable.HashMap
import problem.{Interval, Problem}

abstract class ScenarioFactory {
  def generate(p: Problem): Scenario
}

object MonteCarloScenarioFactory extends ScenarioFactory {
  private val rng: Random = Config.getRNG()

  def generate(p: Problem): Scenario = {
    var result: Map[String, Double] = new HashMap[String, Double]
    p.getIntervals() foreach ((i: Interval) => {
      result += (i.name -> (rng.nextDouble() * (i.upper - i.lower) + i.lower))
    })
    result
  }
}