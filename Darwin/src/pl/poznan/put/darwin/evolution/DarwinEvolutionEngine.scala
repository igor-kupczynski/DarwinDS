package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.Config.{Scenario, Solution}
import scala.Iterator.range
import pl.poznan.put.darwin.model.{SimpleSolutionFactory, MonteCarloScenarioFactory}
import collection.mutable.HashMap
import pl.poznan.put.darwin.experiment.{Experiment, SolutionResult}
import pl.poznan.put.darwin.jrsintegration.JrsIntegration

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

  def start(baseResult: HashMap[Solution, SolutionResult]): List[Tuple2[Solution, SolutionResult]] = {
    generation = 0
    scenarios = null
    solutions = range(0, params.individualCount).map[Solution](idx =>
            {params.solutionFactory.generate(params.problem)}).toList
    var currentResult = sortList(baseResult.toList)
    while (generation < params.generationCount) {
      currentResult = nextGeneration(currentResult)
      generation += 1
    }
    currentResult
  }

  private def nextGeneration(result: List[Tuple2[Solution, SolutionResult]]):List[Tuple2[Solution, SolutionResult]] = {
    if (generation % params.regenerateEveryGenerations == 0) scenarios = regenerateScenarios()
    val parents = DarwinSelectionStrategy.select(result, params.individualCount)
    val children = mutateAll(mateAll(parents))
    mergeGenerations(result, children)
  }

  private def mergeGenerations(parents: List[Tuple2[Solution, SolutionResult]],
                               offspring: List[Solution]): List[Tuple2[Solution, SolutionResult]] = {
    val rankedOffspring = Experiment.perform(params.problem, scenarios, solutions)
    params.scoreKeeper.updateResult(rankedOffspring)
    val offspringList = sortList(rankedOffspring.toList)
    sortList(parents.take(parents.length / 2) ::: offspringList.take(parents.length /2))
  }

  private def sortList(arg: List[Tuple2[Solution, SolutionResult]]): List[Tuple2[Solution, SolutionResult]] = {
    def solutionLessThan(a: Tuple2[Solution, SolutionResult], b: Tuple2[Solution, SolutionResult]): Boolean = {
      val ra: SolutionResult = a._2
      val rb: SolutionResult = b._2
      if (ra.fitness < rb.fitness) true else false
    }
    arg.sort(solutionLessThan)
  }

  private def mutateAll(individuals: List[Solution]): List[Solution] = {
    individuals.map((s: Solution) => DarwinMutation.mutate(params.problem, s, generation))
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