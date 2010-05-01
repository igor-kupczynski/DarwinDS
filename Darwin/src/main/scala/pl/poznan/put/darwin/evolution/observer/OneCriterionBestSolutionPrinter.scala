package pl.poznan.put.darwin.evolution.observer

import pl.poznan.put.darwin.model.solution.EvaluatedSolution


/**
 * Class printing best solution in generation. /using first criterion/
 */
class OneCriterionBestSolutionPrinter extends EvolutionObserver {

  override def notify(params: Map[String, Any]) {
    val generation: List[EvaluatedSolution] = params("generation").asInstanceOf[List[EvaluatedSolution]]

    val number: Int = params("number").asInstanceOf[Int]

    val criterion = generation(0).goals(0)

    var bestS = generation(0)
    var bestVal = bestS.getPercentile(criterion, 0)

    generation foreach ((s: EvaluatedSolution) => {
      if ((criterion.max && s.getPercentile(criterion, 0) > bestVal) ||
          (!criterion.max && s.getPercentile(criterion, 0) < bestVal)) {
        bestS = s
        bestVal = s.getPercentile(criterion, 0)
      }
    })

    println("best[%s]: %s (%s) <= %s".format(criterion.name, bestVal, bestS.utilityFunctionValue, bestS))
   }
}
