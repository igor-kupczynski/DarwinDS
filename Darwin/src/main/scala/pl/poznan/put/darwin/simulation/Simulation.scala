package pl.poznan.put.darwin.simulation

import pl.poznan.put.darwin.evolution.DarwinEvolver
import pl.poznan.put.darwin.model.problem.Problem
import pl.poznan.put.darwin.model.solution._
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.Scenario
import com.weiglewilczek.slf4s.Logging

class Simulation(val config: Config, val problem: Problem) extends Logging {
  
  private var fired = false
  private val evolver = new DarwinEvolver()
  private[simulation] var observers: List[SimulationObserver] = List()

  var scenarios: List[Map[String, Double]] = Nil

  def run(markedSolutions: List[MarkedSolution]): List[EvaluatedSolution] = {
    if (!fired) {
      logger info "Firing simulation"

      fired = true
      for (idx <- 1 to config.SCENARIO_COUNT) {
        scenarios = Scenario.generate(problem) :: scenarios
      }
      logger info "Generated scenarios"
      
      var solutions: List[Solution] = Nil
      for (idx <- 1 to config.SOLUTION_COUNT) {
        solutions = Solution.random(this, scenarios) :: solutions
      }
      logger info "Generated solutions"

      EvaluatedSolution(solutions, scenarios)
    } else {
      logger info "Performing evolution"
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
