package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.Config.Solution
import collection.mutable.HashMap
import pl.poznan.put.darwin.experiment.SolutionResult
import pl.poznan.put.darwin.model.Config
import scala.Iterator.range


/**
 * Class selecting parents for next generation
 *
 * @author Igor Kupczynski
 */
object DarwinSelectionStrategy {

  /**
   * From list of evaluated solutions return list of parents. Two parents for one child.
   */
  def select(individuals: List[Tuple2[Solution, SolutionResult]], childToGenerate: Int): List[Solution] = {
    val probabilities: Array[Double] = computeProbabilities(individuals)

    // 2*|Children| = |Parent|
    range(0, 2*childToGenerate).map(i => {
      val idx = getIndexForProbability(Config.getRNG().nextDouble() * probabilities(probabilities.length - 1), probabilities)
      val indi: Tuple2[Solution, SolutionResult] = individuals(idx)
      indi._1
    }).toList
  }


  private def computeProbabilities(individuals: List[Tuple2[Solution, SolutionResult]]): Array[Double] = {
    val card = individuals.length
    val prob: List[Double] = individuals map {
      case (s: Solution, sr: SolutionResult) => {
        Math.pow((card - sr.fitness + 1).asInstanceOf[Double] / card, Config.GAMMA) -
                Math.pow((card - sr.fitness).asInstanceOf[Double] / card, Config.GAMMA)
      }
    }
    var acc: Double = 0
    (prob map ((p:Double) => {acc += p; acc})).toArray[Double]
  }

  private def getIndexForProbability(p: Double, probabilities: Array[Double]): Int = {
    var idx = 0
    probabilities foreach ((probability: Double) => {
      if (probability > p) return(idx)
      idx += 1
    })
    -1
  }
}
