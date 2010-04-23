package pl.poznan.put.darwin.jrsintegration

import pl.poznan.put.cs.idss.jrs.rules.{Rule, RulesContainer}
import java.util.ArrayList

/**
 * Wrapper on JRS rule container. Extending basic functionality with an option to remember weights.
 *
 * @author: Igor Kupczynski
 */
class DarwinRulesContainer(private val rulesContainer: RulesContainer) {

  def getRules(ruleType: Int, characteristicDecisionClassUsageTip: Int): ArrayList[Rule] =
    rulesContainer.getRules(ruleType, characteristicDecisionClassUsageTip)
}