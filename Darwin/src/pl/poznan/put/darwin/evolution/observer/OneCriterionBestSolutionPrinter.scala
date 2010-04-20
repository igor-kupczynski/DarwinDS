package pl.poznan.put.darwin.evolution.observer

import pl.poznan.put.darwin.model.Solution


/**
 * Class printing best solution in generation. /using first criterion/
 */
class OneCriterionBestSolutionPrinter extends EvolutionObserver {

  println("--- CREATED ---")

  override def notify(params: Map[String, Any]) {
    val generation: List[Solution] = params("generation").asInstanceOf[List[Solution]]

    val number: Int = params("number").asInstanceOf[Int]

    val criterion = generation(0).goals.next()

    var bestS = generation(0)
    var bestVal = bestS.getPercentile(criterion, 0)

    generation foreach ((s: Solution) => {
      if ((criterion.max && s.getPercentile(criterion, 0) > bestVal) ||
          (!criterion.max && s.getPercentile(criterion, 0) < bestVal)) {
        bestS = s
        bestVal = s.getPercentile(criterion, 0)
      }
    })

    println("best[%s]: %s (%s) <= %s".format(criterion.name, bestVal, bestS.utilityFunctionValue, bestS))
   }
}