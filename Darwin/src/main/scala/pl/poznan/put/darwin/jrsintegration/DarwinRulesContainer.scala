package pl.poznan.put.darwin.jrsintegration

import pl.poznan.put.cs.idss.jrs.rules.{Rule, RulesContainer}
import pl.poznan.put.darwin.model.solution.EvaluatedSolution
import pl.poznan.put.allrules.model.{Rule => ARRule}


abstract class AbstractRulesContainer {
  def getScore(solution: EvaluatedSolution): Double
}

  
/**
 * Container for rules generated in JRS. Rules are stored together with their
 * weights. Can be asked about weight of given example (aka. solution)
 *
 * @author: Igor Kupczynski
 */
class DarwinRulesContainer(val rules: List[Tuple2[AbstractRule, Double]])
    extends AbstractRulesContainer {

  /** 
  * Returns score of given solution on set of rules in the container
  */ 
  override def getScore(solution: EvaluatedSolution): Double = {
    rules.foldLeft[Double](0.0)(
      (sum: Double, pair: Tuple2[AbstractRule, Double]) => {
        val r = pair._1
        val w = pair._2
        if (r covers solution)
          sum + w
        else
          sum
      }
    )
  }
}


abstract class AbstractRule {
  def covers(s: EvaluatedSolution): Boolean
}

class JrsRule(rule: Rule) extends AbstractRule {
  override def covers(s: EvaluatedSolution): Boolean = {
    rule covers ExampleFactory(s)
  }
}

class AllRule(rule: ARRule[Any]) extends AbstractRule {
  def covers(s: EvaluatedSolution): Boolean = {
    rule covers ObjectFactory(s)
  }
}
  
trait WeightCalculator {
  def calculateWeights(rulesAtLeast: List[AbstractRule],
                       rulesAtMost: List[AbstractRule],
                               solutions: List[EvaluatedSolution]):
      Map[AbstractRule, Double] = {
    val sim = solutions(0).sim
    var weights: Map[AbstractRule, Double] = Map()
    rulesAtLeast foreach ((rule: AbstractRule) => {
      val count = solutions.filter(s => rule covers s).length
      weights += (rule -> math.pow(1 - sim.config.DELTA, count))
    })
    rulesAtMost foreach ((rule: AbstractRule) => {
      val count = solutions.filter(s => rule covers s).length
      weights += (rule -> (-1) * math.pow(1 - sim.config.DELTA, count))
    })
    weights
  }  
}

object ARRulesContainer extends WeightCalculator {

  def apply(rules: List[ARRule[Any]], examples: List[EvaluatedSolution]):
      DarwinRulesContainer = {
    val sim = examples(0).sim
    val rulesAtLeast: List[AllRule] = rules.filter(_.atLeast) map {new AllRule(_)}
    val rulesAtMost: List[AllRule] = rules.filter(!_.atLeast) map {new AllRule(_)}
    val weights: Map[AbstractRule, Double] =
      calculateWeights(rulesAtLeast, rulesAtMost, examples)
    new DarwinRulesContainer(weights.toList)
  }
}

/**
 * Factory object for creating DarwinRuleContainer and calculating
 * appropriate weights for each rule. Uses only one rules container
 */
object DarwinRulesContainer extends WeightCalculator {

  /**
   * Create new instance of DarwinRuleContainer. Rules will be based on
   * a container and their weights on given example list.
   */
  def apply(rulesContainer: RulesContainer, examples: List[EvaluatedSolution]):
        DarwinRulesContainer = {
    apply(List(rulesContainer), examples)
  }

  /**
   * Create new instance of DarwinRuleContainer. Rules will be based on
   * the given containers and their weights on given example list.
   */
  def apply(rulesContainers: List[RulesContainer], examples: List[EvaluatedSolution]):
        DarwinRulesContainer = {
    val sim = examples(0).sim
    var rulesAtLeast: List[JrsRule] = Nil
    var rulesAtMost: List[JrsRule] = Nil
    for (rulesContainer <- rulesContainers) {
      val ral = rulesContainer.getRules(Rule.CERTAIN, Rule.AT_LEAST)
      if (ral != null) 
        rulesAtLeast = ral.toArray(new Array[Rule](0)).toList.map({new JrsRule(_)}) :::
          rulesAtLeast
      if (sim.config.USE_AT_MOST) {
        val ram = rulesContainer.getRules(Rule.CERTAIN, Rule.AT_MOST)
        if (ram != null)
          rulesAtMost = ram.toArray(new Array[Rule](0)).toList.map({new JrsRule(_)}) :::
            rulesAtMost
      }
    }
    val weights: Map[AbstractRule, Double] =
      calculateWeights(rulesAtLeast, rulesAtMost, examples)
    new DarwinRulesContainer(weights.toList)
  }

}
