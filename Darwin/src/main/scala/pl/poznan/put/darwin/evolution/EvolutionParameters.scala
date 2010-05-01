package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.Problem
import pl.poznan.put.darwin.jrsintegration.DarwinRulesContainer

/**
 * Class for storing evolution experiment parameters
 *
 * @author Igor Kupczynski
 */
case class EvolutionParameters(problem: Problem, rulesContainer: DarwinRulesContainer, config: Config) {

  var individualCount: Int = config.SOLUTION_COUNT
  var scenarioCount: Int = config.SCENARIO_COUNT
  var generationCount: Int = config.GENERATION_COUNT
  var regenerateEveryGenerations: Int = 100000000
  var regeneratePercent: Double = 0.0
}
