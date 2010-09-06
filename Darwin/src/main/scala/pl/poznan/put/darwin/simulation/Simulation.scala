package pl.poznan.put.darwin.simulation

import pl.poznan.put.darwin.evolution.DarwinEvolver
import pl.poznan.put.darwin.model.problem.Problem
import pl.poznan.put.darwin.model.solution._
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.Scenario
  
class Simulation(val config: Config, val problem: Problem) {
  
  private var fired = false
  private val evolver = new DarwinEvolver()
  private[simulation] var observers: List[SimulationObserver] = List()
  
  def run(markedSolutions: List[MarkedSolution]): List[EvaluatedSolution] = {
    if (!fired) {
      fired = true
      var scenarios: List[Map[String, Double]] = Nil
      for (idx <- 1 to config.SCENARIO_COUNT) {
        scenarios = Scenario.generate(problem) :: scenarios
      }
      
      var solutions: List[Solution] = Nil
      for (idx <- 1 to config.SOLUTION_COUNT) {
        solutions = Solution.random(this, scenarios) :: solutions
      }
      EvaluatedSolution(solutions, scenarios)
    } else {
      evolver.preformEvolution(markedSolutions)
    }
  }

  /**
   * Adds observer to list (if not there yet)
   */
  def registerObserver(obs: SimulationObserver) {
    if (!observers.contains(obs)) {
      observers = observers :+ obs
    }
  }

  /** 
   * Removes observer from list 
   */
  def removeObserver(obs: SimulationObserver) {
    observers = observers filterNot obs.==
  }


  /**
   * Informs simulation about new generation in evolution loop
   */
  def postGeneration(solutions: List[RankedSolution]) {
    notifyAll(NewGenerationEvent(solutions))
  }

  /**
   * Informs simulation about DM choices
   */
  def postDMChoices(solutions: List[MarkedSolution]) {
    notifyAll(DMChoiceEvent(solutions))
  }
  
  private def notifyAll(event: SimulationEvent) {
    observers foreach { o => o.notify(event) }
  }

  
}
