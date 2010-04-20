package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.jrsintegration.ScoreKeeper
import pl.poznan.put.darwin.model.Config.Scenario
import pl.poznan.put.darwin.experiment.Experiment
import pl.poznan.put.darwin.model.problem.Problem
import pl.poznan.put.darwin.model.{Solution, MonteCarloScenarioFactory, Config}


/**
 * Class for evaluating solutions population on set of scenarios
 *
 * @author Igor Kupczynski
 */
class DarwinFitnessEvaluator(problem: Problem, scores: ScoreKeeper)  {

  private var scenarios = _regenerate()

  def evaluate(solutions: List[Solution]): List[Solution] = {
    val results = Experiment.perform(problem, scenarios, solutions)
    scores.updateResult(results)
    results
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