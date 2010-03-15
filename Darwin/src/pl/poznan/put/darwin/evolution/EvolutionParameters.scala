package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.{SimpleSolutionFactory, SolutionFactory, Problem}

/**
 * Class for storing evolution experiment parameters
 *
 * @author Igor Kupczynski
 */
case class EvolutionParameters(problem: Problem) {

  var individualCount: Int = 30
  var scenarioCount: Int = 40
  var generationCount: Int = 30
  var regenerateEveryGenerations: Int = 10
  var regeneratePercent: Double = 0.5

  val solutionFactory: SolutionFactory = SimpleSolutionFactory
}