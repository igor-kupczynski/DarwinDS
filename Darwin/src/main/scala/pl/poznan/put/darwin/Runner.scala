package pl.poznan.put.darwin.model

import pl.poznan.put.darwin.simulation.Simulation
  
object Runner {
  def main(args: Array[String]) {
    (new Simulation).run()
  }
}

