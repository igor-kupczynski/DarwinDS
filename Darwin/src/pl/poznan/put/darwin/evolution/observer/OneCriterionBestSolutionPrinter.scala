package pl.poznan.put.darwin.evolution.observer

import collection.mutable.HashMap
import pl.poznan.put.darwin.experiment.SolutionResult
import pl.poznan.put.darwin.model.Config.Solution


/**
 * Class printing best solution in generation. /using first criterion/
 */
class OneCriterionBestSolutionPrinter extends EvolutionObserver {

  println("--- CREATED ---")

  override def notify(params: HashMap[String, Any]) {
    val generation: List[Tuple2[Solution, SolutionResult]] = params("generation").asInstanceOf[List[Tuple2[Solution, SolutionResult]]]

    val number: Int = params("number").asInstanceOf[Int]

    val criterion = generation(0)._2.goals().next()

    var bestS = generation(0)._1
    var bestSR = generation(0)._2
    var bestVal = bestSR.getPercentile(criterion, 0)

    generation foreach {case (s, sr: SolutionResult) => {
      if ((criterion.max && sr.getPercentile(criterion, 0) > bestVal) ||
          (!criterion.max && sr.getPercentile(criterion, 0) < bestVal)) {
        bestSR = sr
        bestS = s
        bestVal = sr.getPercentile(criterion, 0)
      }
    }}

    println("best[%s]: %s <= %s".format(criterion.name, bestVal, bestS))
   }
}