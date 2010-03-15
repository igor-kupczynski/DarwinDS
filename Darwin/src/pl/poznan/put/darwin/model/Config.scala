package pl.poznan.put.darwin.model

import java.util.Random

object Config {
  type Scenario = String => Double
  type Solution = String => Double

  private val rng: Random = new Random()
  def getRNG(): Random = {
    rng
  }

  val MAX_VARIABLE: Int = 20
  val SOLUTION_COUNT: Int = 30
  val SCENARIO_COUNT: Int = 30

  val GOOD_COUNT: Int = 10

  val PERCENTILES: List[Int] = 1 :: 25 :: 50 :: Nil;

  val DELTA = 0.1
  val GAMMA = 2
}