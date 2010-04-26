package pl.poznan.put.darwin.model

import pl.poznan.put.darwin.simulation.Simulation
  
object Runner {
  def main(args: Array[String]) {
    val config = new Config()
    (new Simulation(config)).run()
  }
}

