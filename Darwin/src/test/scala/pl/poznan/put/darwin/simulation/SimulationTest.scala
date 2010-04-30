package pl.poznan.put.darwin.simulation

import org.specs._
import org.specs.mock.Mockito
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.Problem
import pl.poznan.put.darwin.model.solution.{MarkedSolution, RankedSolution}

class SimulationTest extends Specification with Mockito {

  "Simulation" should {
    val sim = prepareSimulation()
    "allow observers to be registered/unregistered" in {
      val obs1 = mock[SimulationObserver]
      val obs2 = mock[SimulationObserver]
      sim.observers.length must be_==(0)
      sim.registerObserver(obs1)
      sim.observers.length must be_==(1)
      sim.registerObserver(obs2)
      sim.observers.length must be_==(2)
      sim.registerObserver(obs1)
      sim.observers.length must be_==(2)
      sim.removeObserver(obs1)
      sim.observers.length must be_==(1)
      sim.removeObserver(obs1)
      sim.observers.length must be_==(1)
      sim.removeObserver(obs2)
      sim.observers.length must be_==(0)
    }
    "post new generations to registered observers through NewGenerationEvent" in {
      val obs1 = mock[SimulationObserver]
      val obs2 = mock[SimulationObserver]
      val newGeneration = mock[List[RankedSolution]]
      sim.registerObserver(obs1)
      sim.registerObserver(obs2)
      sim.postGeneration(newGeneration)
      there was one(obs1).notify(NewGenerationEvent(newGeneration))
      there was one(obs2).notify(NewGenerationEvent(newGeneration))
    }
    "post new generations to registered observers through NewGenerationEvent" in {
      val obs1 = mock[SimulationObserver]
      val obs2 = mock[SimulationObserver]
      val marked = mock[List[MarkedSolution]]
      sim.registerObserver(obs1)
      sim.registerObserver(obs2)
      sim.postDMChoices(marked)
      there was one(obs1).notify(DMChoiceEvent(marked))
      there was one(obs2).notify(DMChoiceEvent(marked))
    }
  }

  def prepareSimulation: Simulation = {
    val config = mock[Config]
    val problem = mock[Problem]
    new Simulation(config, problem)
  }
  
}
