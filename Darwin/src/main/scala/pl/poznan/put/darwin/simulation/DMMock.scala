package pl.poznan.put.darwin.simulation

import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.solution.{MarkedSolution, EvaluatedSolution}
import pl.poznan.put.darwin.utils.{RNG, ListUtils}

class DMMock(sim: Simulation) {

  def apply(items: List[EvaluatedSolution]): List[MarkedSolution] = {
    val sortedItems = items.sortWith((one, other) =>
      one.utilityFunctionValue > other.utilityFunctionValue)

    val noisedItems = addNoise(sortedItems, sim.config.NOISE_LEVEL)
  
    val toSelect = getGoodCount(sim.config.BASE_GOOD_COUNT,
                                sim.config.GOOD_COUNT_DELTA)
  
    var idx = -1
    val marked = noisedItems.map((s: EvaluatedSolution) => {
      idx += 1
      if (idx < toSelect) MarkedSolution(s, true) else MarkedSolution(s, false)
    })
    sim.postDMChoices(marked)
    marked
  }

  private[simulation] def getGoodCount(baseGood: Int, goodDelta: Int): Int = {
    val delta: Int = if (goodDelta > 0) {
      RNG.get().nextInt(goodDelta + 1) *
        (if (RNG.get().nextBoolean()) -1 else 1)
    } else 0
    baseGood + delta
  }

  private[simulation] def addNoise(items: List[EvaluatedSolution], noiseLevel: Int): List[EvaluatedSolution] = {
    if (noiseLevel > 0) {
      val (a, b) = items.splitAt(noiseLevel)
      ListUtils.shuffle(a) ::: b
    } else items
  }
}
