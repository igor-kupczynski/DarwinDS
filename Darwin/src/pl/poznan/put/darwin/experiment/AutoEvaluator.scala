package pl.poznan.put.darwin.experiment

import java.util.{Collections}
import pl.poznan.put.darwin.model.Config.Solution
import collection.mutable.HashMap
import pl.poznan.put.darwin.model.Config

object AutoEvaluator {

  private def autoCompare(one: SolutionResult, other: SolutionResult): Boolean =  {
      val valA = one.autoValue
      val valB = other.autoValue
      if (valA < valB) true else false
    }

  def evaluate(items: HashMap[Solution, SolutionResult]): HashMap[Solution, SolutionResult] = {


    val sols: List[SolutionResult] = List.fromIterator(items.values)
    sols.sort(autoCompare)
    
    for (idx <- Iterator.range(0, Config.GOOD_COUNT)) {
       sols(idx).isGood = true
    }

    items
  }
}