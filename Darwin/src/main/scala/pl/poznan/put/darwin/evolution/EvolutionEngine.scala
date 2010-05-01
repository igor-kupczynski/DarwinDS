package pl.poznan.put.darwin.evolution

import observer.EvolutionObserver
import pl.poznan.put.darwin.model.Scenario
import pl.poznan.put.darwin.model.solution.{EvaluatedSolution, RankedSolution}

/**
 * Main class performing the evolution
 *
 * @author Igor Kupczynski
 */
class EvolutionEngine(params: EvolutionParameters) {
  private var generation: Int = _
  private var scenarios: List[Map[String, Double]] = _
  private var generationObservers: List[EvolutionObserver] = Nil

  def start(input: List[EvaluatedSolution]): List[RankedSolution] = {
    val sim = input(0).sim
  
    generation = 0
    scenarios = null

    var parents: List[RankedSolution] = RankedSolution(input, params.rulesContainer)
    sim.postGeneration(parents)
    var children: List[RankedSolution] = null
    
    while (generation < params.generationCount) {
      children = nextGeneration(parents)
      parents = RankedSolution(parents.take(parents.length / 2) ::: children.take(parents.length /2),
                               params.rulesContainer)
      generation += 1
      sim.postGeneration(parents)
    }
    parents
  }

  def registerGenerationObserver(obs: EvolutionObserver) {
    generationObservers = obs :: generationObservers
  }

  private def notifyGenerationObservers(number: Int, generation: List[RankedSolution]) {
    var params: Map[String, Any] = Map()
    params += ("number" -> number.asInstanceOf[Any])
    params += ("generation" -> generation)
    generationObservers.foreach((o: EvolutionObserver) => o.notify(params))
  }

  private def nextGeneration(result: List[RankedSolution]):List[RankedSolution] = {
    if (generation % params.regenerateEveryGenerations == 0) scenarios = regenerateScenarios()
    val chosenOnes = SelectionStrategy(result, params.individualCount)
    RankedSolution(
      (0 to (chosenOnes.length / 2 - 1)).map(idx => {
        EvaluatedSolution(
          Mutate(
            CrossOver(chosenOnes(2*idx), chosenOnes(2*idx+1)),
            generation),
          scenarios)
      }).toList,
      params.rulesContainer)
  }


  private def regenerateScenarios(): List[Map[String, Double]] = {
    val toRegenerate: Double = if (scenarios == null) 1.0 else params.regeneratePercent
    var newScenarios: List[Map[String, Double]] =
      (0 to (toRegenerate * params.scenarioCount).asInstanceOf[Int] -1).map(
          idx => Scenario.generate(params.problem)
       ).toList
    val left = params.scenarioCount - newScenarios.length
    if (left > 0) {
      scenarios.takeRight(left) ::: newScenarios
    } else {
      newScenarios
    }
  }

}
