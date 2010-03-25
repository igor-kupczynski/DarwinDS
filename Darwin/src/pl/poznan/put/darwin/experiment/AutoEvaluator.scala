package pl.poznan.put.darwin.experiment

import java.util.{Collections}
import pl.poznan.put.darwin.model.Config.Solution
import collection.mutable.HashMap
import pl.poznan.put.darwin.model.Config

object AutoEvaluator {
  private def compareByResultAutovalue(one: Tuple2[Solution, SolutionResult],
                                       other: Tuple2[Solution, SolutionResult]): Boolean = {
    one._2.autoValue >= other._2.autoValue // from highest to lowest
  }

  def evaluate(items: HashMap[Solution, SolutionResult]): List[Tuple2[Solution, SolutionResult]] = {
    evaluate(items.toList)
  }

  def evaluate(items: List[Tuple2[Solution, SolutionResult]]): List[Tuple2[Solution, SolutionResult]] = {

    val sortedItems = items.sort(compareByResultAutovalue)

    for (idx <- Iterator.range(0, Config.GOOD_COUNT)) {
      sortedItems(idx)._2.isGood = true
    }

    sortedItems
  }
}