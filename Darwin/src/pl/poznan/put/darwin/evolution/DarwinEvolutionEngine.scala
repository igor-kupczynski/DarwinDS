package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.Config.{Scenario, Solution}
import scala.Iterator.range
import pl.poznan.put.darwin.model.{SimpleSolutionFactory, MonteCarloScenarioFactory}
import collection.mutable.HashMap
import pl.poznan.put.darwin.experiment.SolutionResult

/**
 * Main class performing the evolution
 *
 * @author Igor Kupczynski
 */
class DarwinEvolutionEngine(params: EvolutionParameters) {
  private var generation: Int = _
  private var scenarios: List[Scenario] = _
  private var solutions: List[Solution] = _
  private val crossOver = new DarwinCrossOver(params.problem)

  def start(baseResult: HashMap[Solution, SolutionResult]) {
    generation = 0
    scenarios = null
    solutions = range(0, params.individualCount).map[Solution](idx =>
            {params.solutionFactory.generate(params.problem)}).toList
    var currentResult = baseResult
    while (generation < params.generationCount) {
      currentResult = nextGeneration(currentResult)
      generation += 1
    }
  }

  private def nextGeneration(result: HashMap[Solution, SolutionResult]): HashMap[Solution, SolutionResult] = {
    if (generation % params.regenerateEveryGenerations == 0) scenarios = regenerateScenarios()
    val parents = DarwinSelectionStrategy.select(result.toList, params.individualCount)
    val children = mateAll(parents)

    null
  }

  private def mutate() {
    
  }

  private def mateAll(parents: List[Solution]): List[Solution] = {
    range(0, parents.length / 2).map((idx: Int) => {
      crossOver.mate(parents(2*idx), parents(2*idx+1))
    }).toList
  }

  private def regenerateScenarios(): List[Scenario] = {
    val toRegenerate: Double = if (scenarios == null) 1.0 else params.regeneratePercent
    var newScenarios: List[Scenario] = range(0, (toRegenerate * params.scenarioCount).asInstanceOf[Int]).map[Scenario](
      idx => MonteCarloScenarioFactory.generate(params.problem)
      ) .toList
    val left = params.scenarioCount - newScenarios.length
    if (left > 0) {
      scenarios.takeRight(left) ::: newScenarios
    } else {
      newScenarios
    }
  }

}