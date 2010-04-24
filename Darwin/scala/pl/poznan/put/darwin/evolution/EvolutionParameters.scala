package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.problem.Problem
import pl.poznan.put.darwin.jrsintegration.DarwinRulesContainer

/**
 * Class for storing evolution experiment parameters
 *
 * @author Igor Kupczynski
 */
case class EvolutionParameters(problem: Problem, rulesContainer: DarwinRulesContainer) {

  var individualCount: Int = 30
  var scenarioCount: Int = 40
  var generationCount: Int = 60
  var regenerateEveryGenerations: Int = 100000000
  var regeneratePercent: Double = 0.0
}