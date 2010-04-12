package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.{SimpleSolutionFactory, SolutionFactory}
import pl.poznan.put.darwin.jrsintegration.ScoreKeeper
import pl.poznan.put.darwin.model.problem.Problem

/**
 * Class for storing evolution experiment parameters
 *
 * @author Igor Kupczynski
 */
case class EvolutionParameters(problem: Problem, scoreKeeper: ScoreKeeper) {

  var individualCount: Int = 30
  var scenarioCount: Int = 40
  var generationCount: Int = 60
  var regenerateEveryGenerations: Int = 100000000
  var regeneratePercent: Double = 0.0

  val solutionFactory: SolutionFactory = SimpleSolutionFactory
}