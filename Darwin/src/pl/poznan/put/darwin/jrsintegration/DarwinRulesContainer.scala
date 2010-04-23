package pl.poznan.put.darwin.jrsintegration

import pl.poznan.put.cs.idss.jrs.rules.{Rule, RulesContainer}
import pl.poznan.put.darwin.model.solution.EvaluatedSolution
import pl.poznan.put.darwin.model.Config

/**
 * Container for rules generated in JRS. Rules are stored together with their weights.
 *
 * @author: Igor Kupczynski
 */
class DarwinRulesContainer(val rules: List[Tuple2[Rule, Double]]) {
}

/**
 * Companion object for creating DarwinRuleContainer and calculating appropriate weights for each rule
 */
object DarwinRulesContainer {

  /**
   * Create new instance of DarwinRuleContainer. Rules will be based on
   * a container and their weights on given example list.
   */
  def apply(rulesContainer: RulesContainer, examples: List[EvaluatedSolution]):
        DarwinRulesContainer = {
    val rules: List[Rule] = rulesContainer.getRules(Rule.CERTAIN, Rule.AT_LEAST).toArray(new Array[Rule](0)).toList
    val weights: Map[Rule, Double] = calculateWeights(rules, examples)
    new DarwinRulesContainer(weights.toList)
  }

  private def calculateWeights(rules: List[Rule], solutions: List[EvaluatedSolution]): Map[Rule, Double] = {
    var weights: Map[Rule, Double] = Map()
    rules foreach ((rule: Rule) => {
      val count = solutions.filter(s => rule covers ExampleFactory(s)).length
      weights += (rule -> Math.pow(1 - Config.DELTA, count))
    })
    weights
  }
}