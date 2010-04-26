package pl.poznan.put.darwin.simulation

import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.solution.{MarkedSolution, EvaluatedSolution}

object DMMock {
  def apply(items: List[EvaluatedSolution]): List[MarkedSolution] = {
    val sortedItems = items.sortWith((one, other) => one.utilityFunctionValue > other.utilityFunctionValue)

    var idx = -1
    sortedItems.map((s: EvaluatedSolution) => {
      idx += 1
      if (idx < Config.GOOD_COUNT) MarkedSolution(s, true) else MarkedSolution(s, false)
    })
  }
}
