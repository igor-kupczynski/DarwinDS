package pl.poznan.put.darwin.model

import scala.util.Random

object Config {
  type Scenario = String => Double
  type Solution = String => Double

  def getRNG(): Random = {
    new Random()
  }

  val MAX_VARIABLE: Int = 20
  val SOLUTION_COUNT: Int = 30
  val SCENARIO_COUNT: Int = 30

  val GOOD_COUNT: Int = 10

  val PERCENTILES: List[Int] = 1 :: 25 :: 50 :: Nil;

  val DELTA = 0.1
  val GAMMA = 2
}