package pl.poznan.put.darwin.evolution.observer

/**
 * Abstract base class for evolution observers
 *
 * @author: Igor Kupczynski
 */
abstract class EvolutionObserver {

  def notify(params: Map[String, Any])
}