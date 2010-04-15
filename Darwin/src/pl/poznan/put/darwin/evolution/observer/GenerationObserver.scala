package pl.poznan.put.darwin.evolution.observer

import pl.poznan.put.darwin.model.Config.Solution
import collection.mutable.HashMap
import pl.poznan.put.darwin.experiment.SolutionResult

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

  def notify(params: HashMap[String, Any]) {
    val generation: List[Tuple2[Solution, SolutionResult]] = params("generation").asInstanceOf[List[Tuple2[Solution, SolutionResult]]]

    val number: Int = params("number").asInstanceOf[Int]

    var ps: List[Double] = Nil
    var autoValue: List[Double] = Nil
    generation foreach {case (s: Solution, sr: SolutionResult) => {
      ps = sr.primaryScore :: ps
      autoValue = sr.utilityFunctionValue :: autoValue
    }}

    val minPs = ps reduceLeft min
    val minAutoValue = autoValue reduceLeft min

    val maxPs = ps reduceLeft max
    val maxAutoValue = autoValue reduceLeft max

    val avgPs = (ps reduceLeft sum) / ps.length
    val avgAutoValue = (autoValue reduceLeft sum) / autoValue.length

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