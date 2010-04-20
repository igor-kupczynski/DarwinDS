package pl.poznan.put.darwin.evolution

import observer.EvolutionObserver
import pl.poznan.put.darwin.model.Config.Scenario
import collection.immutable.HashMap
import pl.poznan.put.darwin.experiment.Experiment
import pl.poznan.put.darwin.model.{Solution, MonteCarloScenarioFactory}

/**
 * Main class performing the evolution
 *
 * @author Igor Kupczynski
 */
class DarwinEvolutionEngine(params: EvolutionParameters) {
  private var generation: Int = _
  private var scenarios: List[Scenario] = _
  private var solutions: List[Solution] = _
  private var generationObservers: List[EvolutionObserver] = Nil

  def start(baseResult: List[Solution]): List[Solution] = {
    generation = 0
    scenarios = null
    solutions = (0 to params.individualCount-1).map[Solution](idx =>
            {params.solutionFactory.generate(params.problem)}).toList
    var currentResult = sortList(baseResult)
    while (generation < params.generationCount) {
      notifyGenerationObservers(generation, currentResult)
      currentResult = nextGeneration(currentResult)
      generation += 1
    }
    currentResult
  }

  def registerGenerationObserver(obs: EvolutionObserver) {
    generationObservers = obs :: generationObservers
  }

  private def notifyGenerationObservers(number: Int, generation: List[Solution]) {
    var params: Map[String, Any] = new HashMap[String, Any]()
    params += ("number" -> number.asInstanceOf[Any])
    params += ("generation" -> generation)
    generationObservers.foreach((o: EvolutionObserver) => o.notify(params))
  }

  private def nextGeneration(result: List[Solution]):List[Solution] = {
    if (generation % params.regenerateEveryGenerations == 0) scenarios = regenerateScenarios()
    val parents = DarwinSelectionStrategy.select(result, params.individualCount)
    val children = mutateAll(mateAll(parents))
    mergeGenerations(result, children)
  }

  private def mergeGenerations(parents: List[Solution],
                               offspring: List[Solution]): List[Solution] = {
    val rankedOffspring = Experiment.perform(params.problem, scenarios, solutions)
    params.scoreKeeper.updateResult(rankedOffspring)
    val offspringList = sortList(rankedOffspring)
    sortList(
      (parents.take(parents.length / 2) map (s => new Solution(params.problem, s.values))) :::
      offspringList.take(parents.length /2)
    )
  }

  private def sortList(arg: List[Solution]): List[Solution] = {
    def solutionLessThan(a: Solution, b: Solution): Boolean = {
      if (a.getFitness() < b.getFitness()) true else false
    }
    arg.sort(solutionLessThan)
  }

  private def mutateAll(individuals: List[Solution]): List[Solution] = {
    individuals.map((s: Solution) => {
      DarwinMutation.mutate(s, generation)
    })
  }

  private def mateAll(parents: List[Solution]): List[Solution] = {
    (0 to (parents.length / 2 - 1)).map((idx: Int) => {
      DarwinCrossOver.mate(parents(2*idx), parents(2*idx+1))
    }).toList
  }

  private def regenerateScenarios(): List[Scenario] = {
    val toRegenerate: Double = if (scenarios == null) 1.0 else params.regeneratePercent
    var newScenarios: List[Scenario] = (0 to (toRegenerate * params.scenarioCount).asInstanceOf[Int] -1).map[Scenario](
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