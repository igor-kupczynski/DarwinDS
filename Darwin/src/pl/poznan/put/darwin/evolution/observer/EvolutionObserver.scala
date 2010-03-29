package pl.poznan.put.darwin.evolution.observer

import collection.mutable.HashMap

/**
 * Abstract base class for evolution observers
 *
 * @author: Igor Kupczynski
 */
abstract class EvolutionObserver {

  def notify(params: HashMap[String, Any])
}