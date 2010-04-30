package pl.poznan.put.darwin.simulation

/**
 * Contract for simulation observer capable of reciving notifications on
 * simulation's events
 *
 * @author: Igor Kupczynski
 */
trait SimulationObserver {

  /**
   * Informs observer that certain even has occured
   */
  def notify(event: SimulationEvent)
}
