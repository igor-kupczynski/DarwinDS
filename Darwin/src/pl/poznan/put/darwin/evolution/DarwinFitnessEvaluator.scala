package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.jrsintegration.ScoreKeeper
import pl.poznan.put.darwin.model.Config.{Solution, Scenario}
import pl.poznan.put.darwin.model.{MonteCarloScenarioFactory, Config, Problem}
import collection.mutable.HashMap
import pl.poznan.put.darwin.experiment.{Experiment, SolutionResult}


/**
 * Class for evaluating solutions population on set of scenarios
 *
 * @author Igor Kupczynski
 */
class DarwinFitnessEvaluator(problem: Problem, scores: ScoreKeeper)  {

  private var scenarios = _regenerate()

  def evaluate(solutions: List[Solution]): HashMap[Solution, SolutionResult] = {
    val results: HashMap[Solution, SolutionResult] = Experiment.perform(problem, scenarios, solutions)
    scores.updateResult(results)   
  }

  def regenerate() = {
    scenarios = _regenerate()
  }

  private def _regenerate(): List[Scenario] = {
    Iterator.range(0, Config.SCENARIO_COUNT).map((idx: Int) => {
      MonteCarloScenarioFactory.generate(problem)
    }).toList
  }
}