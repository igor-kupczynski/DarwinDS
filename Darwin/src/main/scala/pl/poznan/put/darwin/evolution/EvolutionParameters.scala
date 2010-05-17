package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.Problem
import pl.poznan.put.darwin.jrsintegration.DarwinRulesContainer

/**
 * Class for storing evolution experiment parameters
 *
 * @author Igor Kupczynski
 */
case class EvolutionParameters(problem: Problem, rulesContainer: DarwinRulesContainer, config: Config)
