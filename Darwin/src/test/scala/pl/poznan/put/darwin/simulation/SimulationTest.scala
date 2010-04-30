package pl.poznan.put.darwin.simulation

import org.specs._
import org.specs.mock.Mockito
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.Problem

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
  }

  def prepareSimulation: Simulation = {
    val config = mock[Config]
    val problem = mock[Problem]
    new Simulation(config, problem)
  }
  
}
