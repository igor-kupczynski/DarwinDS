package pl.poznan.put.darwin.jrsintegration

import pl.poznan.put.cs.idss.jrs.rules.{Rule, RulesContainer}
import pl.poznan.put.darwin.model.solution.EvaluatedSolution
import pl.poznan.put.darwin.model.Config

/**
 * Container for rules generated in JRS. Rules are stored together with their
 * weights. Can be asked about weight of given example (aka. solution)
 *
 * @author: Igor Kupczynski
 */
class DarwinRulesContainer(val rules: List[Tuple2[Rule, Double]]) {

  /** 
  * Returns score of given solution on set of rules in the container
  */ 
  def getScore(solution: EvaluatedSolution): Double = {
    rules.foldLeft[Double](0.0)(
      (sum: Double, pair: Tuple2[Rule, Double]) => {
        val r = pair._1
        val w = pair._2
        if (r covers ExampleFactory(solution))
          sum + w
        else
          sum
      }
    )
  }
}

/**
 * Factory object for creating DarwinRuleContainer and calculating
 * appropriate weights for each rule. Uses only one rules container
 */
object DarwinRulesContainer {

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
    var rulesAtLeast: List[Rule] = Nil
    var rulesAtMost: List[Rule] = Nil
    for (rulesContainer <- rulesContainers) {
      val ral = rulesContainer.getRules(Rule.CERTAIN, Rule.AT_LEAST)
      if (ral != null) 
        rulesAtLeast = ral.toArray(new Array[Rule](0)).toList ::: rulesAtLeast
      if (sim.config.USE_AT_MOST) {
        val ram = rulesContainer.getRules(Rule.CERTAIN, Rule.AT_MOST)
        if (ram != null)
          rulesAtMost = ram.toArray(new Array[Rule](0)).toList ::: rulesAtMost
      }
    }
    val weights: Map[Rule, Double] = calculateWeights(rulesAtLeast, rulesAtMost, examples)
    new DarwinRulesContainer(weights.toList)
  }

  private def calculateWeights(rulesAtLeast: List[Rule], rulesAtMost: List[Rule],
                               solutions: List[EvaluatedSolution]):
      Map[Rule, Double] = {
    val sim = solutions(0).sim
    var weights: Map[Rule, Double] = Map()
    rulesAtLeast foreach ((rule: Rule) => {
      val count = solutions.filter(s => rule covers ExampleFactory(s)).length
      weights += (rule -> math.pow(1 - sim.config.DELTA, count))
    })
    rulesAtMost foreach ((rule: Rule) => {
      val count = solutions.filter(s => rule covers ExampleFactory(s)).length
      weights += (rule -> (-1) * math.pow(1 - sim.config.DELTA, count))
    })
    weights
  }
}
