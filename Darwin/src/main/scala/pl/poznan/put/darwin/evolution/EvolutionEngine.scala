package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.jrsintegration.DarwinRulesContainer
import pl.poznan.put.darwin.model.Scenario
import pl.poznan.put.darwin.model.solution.{EvaluatedSolution, RankedSolution}
import pl.poznan.put.darwin.simulation.Simulation

/**
 * Main class performing the evolution
 *
 * @author Igor Kupczynski
 */
class EvolutionEngine(sim: Simulation, rulesContainer: DarwinRulesContainer) {
  private var generation: Int = _
  private var scenarios: List[Map[String, Double]] = _

  def start(input: List[EvaluatedSolution]): List[RankedSolution] = {
    generation = 0
    scenarios = null

    var parents: List[RankedSolution] = RankedSolution(input, rulesContainer)
    sim.postGeneration(parents)
    var children: List[RankedSolution] = null
    
    while (generation < sim.config.GENERATION_COUNT) {
      children = nextGeneration(parents)
      parents = RankedSolution(parents.take(parents.length / 2) ::: children.take(parents.length /2),
                               rulesContainer)
      generation += 1
      sim.postGeneration(parents)
    }
    parents
  }

  private def nextGeneration(result: List[RankedSolution]):List[RankedSolution] = {
    val regenerateMod = sim.config.REGENERATE_SCENARIONS_EVERY_GENERATIONS
    val indCount = sim.config.SOLUTION_COUNT
    if (generation % regenerateMod == 0) scenarios = regenerateScenarios()
    val chosenOnes = SelectionStrategy(result, indCount)
    RankedSolution(
      (0 to (chosenOnes.length / 2 - 1)).map(idx => {
        EvaluatedSolution(
          Mutate(
            CrossOver(chosenOnes(2*idx), chosenOnes(2*idx+1), scenarios),
            scenarios,
            generation),
          scenarios)
      }).toList,
      rulesContainer)
  }


  private def regenerateScenarios(): List[Map[String, Double]] = {
    val scenarioCount = sim.config.SCENARIO_COUNT
    val toRegenerate: Double = if (scenarios == null) 1.0 else sim.config.REGENERATE_PERCENT_OF_SCENARIONS
    var newScenarios: List[Map[String, Double]] =
      (0 to (toRegenerate * scenarioCount).asInstanceOf[Int] -1).map(
          idx => Scenario.generate(sim.problem)
       ).toList
    val left = scenarioCount - newScenarios.length
    if (left > 0) {
      scenarios.takeRight(left) ::: newScenarios
    } else {
      newScenarios
    }
  }

}
