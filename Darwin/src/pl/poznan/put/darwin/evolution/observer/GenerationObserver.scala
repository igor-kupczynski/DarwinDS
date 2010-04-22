package pl.poznan.put.darwin.evolution.observer

import pl.poznan.put.darwin.model.solution.RankedSolution

/**
 * Observer being notified about new generations prints some statistics about the generation
 *
 * @author: Igor Kupczynski
 */
class GenerationObserver(val loop: Int) extends EvolutionObserver {

  private def min(a: Double, b: Double): Double = {
    if (a < b) a else b
  }

  private def max(a: Double, b: Double): Double = {
    if (a > b) a else b
  }

  private def sum(a: Double, b: Double): Double = {
    a + b
  }

  def notify(params: Map[String, Any]) {
    val generation: List[RankedSolution] = params("generation").asInstanceOf[List[RankedSolution]]

    val number: Int = params("number").asInstanceOf[Int]

    var ps: List[Double] = Nil
    var utilityFuncValue: List[Double] = Nil
    generation foreach ((s: RankedSolution) => {
      ps = s.primaryScore :: ps
      utilityFuncValue = s.utilityFunctionValue :: utilityFuncValue
    })

    val minPs = ps reduceLeft min
    val minAutoValue = utilityFuncValue reduceLeft min

    val maxPs = ps reduceLeft max
    val maxAutoValue = utilityFuncValue reduceLeft max

    val avgPs = (ps reduceLeft sum) / ps.length
    val avgAutoValue = (utilityFuncValue reduceLeft sum) / utilityFuncValue.length

    println("%d,%d,%f,%f,%f,%f,%f,%f".format(
      loop,number,
      maxAutoValue, avgAutoValue, minAutoValue,
      maxPs, avgPs, minPs
      ))
  }
}

/**
 * Companion object for GenerationObserver
 */
object GenerationObserver {

  var loop = -1

  def apply(): GenerationObserver = {
    loop += 1
    new GenerationObserver(loop)
  }
}