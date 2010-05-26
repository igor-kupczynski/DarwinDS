package pl.poznan.put.darwin.simulation

import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.solution.{MarkedSolution, EvaluatedSolution}
import pl.poznan.put.darwin.utils.RNG

class DMMock(sim: Simulation) {

  def apply(items: List[EvaluatedSolution]): List[MarkedSolution] = {
    val sortedItems = items.sortWith((one, other) =>
      one.utilityFunctionValue > other.utilityFunctionValue)

    val toSelect = getGoodCount
  
    var idx = -1
    val marked = sortedItems.map((s: EvaluatedSolution) => {
      idx += 1
      if (idx < toSelect) MarkedSolution(s, true) else MarkedSolution(s, false)
    })
    sim.postDMChoices(marked)
    marked
  }

  private def getGoodCount(): Int = {
    val base: Int = sim.config.BASE_GOOD_COUNT
    val delta: Int = if (0 < sim.config.GOOD_COUNT_DELTA) {
      RNG.get().nextInt(sim.config.GOOD_COUNT_DELTA + 1) *
          (if (RNG.get().nextBoolean()) -1 else 1)
    } else 0
    base + delta
  }
}
