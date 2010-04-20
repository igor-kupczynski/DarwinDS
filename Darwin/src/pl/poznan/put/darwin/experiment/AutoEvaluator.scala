package pl.poznan.put.darwin.experiment

import pl.poznan.put.darwin.model.{Solution, Config}

object AutoEvaluator {
  private def compareByResultUtilityFunctionValue(one: Solution,
                                       other: Solution): Boolean = {
    one.utilityFunctionValue >= other.utilityFunctionValue // from highest to lowest
  }

  def evaluate(items: List[Solution]): List[Solution] = {
    val sortedItems = items.sort(compareByResultUtilityFunctionValue)

    for (idx <- 0 to sortedItems.length) {
      if (idx < Config.GOOD_COUNT) {
        sortedItems(idx).markGood()
      } else {
        sortedItems(idx).markNotGood()
      }
    }

    sortedItems
  }
}