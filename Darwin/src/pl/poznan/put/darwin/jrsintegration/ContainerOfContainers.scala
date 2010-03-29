package pl.poznan.put.darwin.jrsintegration

import pl.poznan.put.cs.idss.jrs.rules.{Rule, RulesContainer}
import collection.jcl.MutableIterator.Wrapper

/**
 * Container containing another rule containers. This way we can have more rules.
 */
class ContainerOfContainers {
  implicit def javaIteratorToScalaIterator[A](it: java.util.Iterator[A]) = new Wrapper(it)
  
  private var containers: List[RulesContainer] = Nil

  def addContainer(c: RulesContainer) {
    containers = c :: containers
  }

  def getRules(): List[Rule] = {
    var result: List[Rule] = Nil
    containers foreach ((rc: RulesContainer) => {
      result = rc.getRules(Rule.CERTAIN, Rule.AT_LEAST).iterator().toList ::: result
    })
    result
  }
}