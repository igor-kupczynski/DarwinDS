package pl.poznan.put.darwin.experiment

import java.util.{Collections}
import pl.poznan.put.darwin.model.Config.Solution
import collection.mutable.HashMap
import pl.poznan.put.darwin.model.Config

object AutoEvaluator {
  private def compareByResultUtilityFunctionValue(one: Tuple2[Solution, SolutionResult],
                                       other: Tuple2[Solution, SolutionResult]): Boolean = {
    one._2.utilityFunctionValue >= other._2.utilityFunctionValue // from highest to lowest
  }

  def evaluate(items: HashMap[Solution, SolutionResult]): List[Tuple2[Solution, SolutionResult]] = {
    evaluate(items.toList)
  }

  def evaluate(items: List[Tuple2[Solution, SolutionResult]]): List[Tuple2[Solution, SolutionResult]] = {

    val sortedItems = items.sort(compareByResultUtilityFunctionValue)

    for (idx <- Iterator.range(0, Config.GOOD_COUNT)) {
      sortedItems(idx)._2.isGood = true
    }

    sortedItems
  }
}