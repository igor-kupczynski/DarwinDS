package pl.poznan.put.darwin.evolution

import scala.Iterator.range
import pl.poznan.put.darwin.model.{Solution, Config}


/**
 * Class selecting parents for next generation
 *
 * @author Igor Kupczynski
 */
object DarwinSelectionStrategy {

  /**
   * From list of evaluated solutions return list of parents. Two parents for one child.
   */
  def select(individuals: List[Solution], childToGenerate: Int): List[Solution] = {
    val probabilities: Array[Double] = computeProbabilities(individuals)

    // 2*|Children| = |Parent|
    range(0, 2*childToGenerate).map(i => {
      val idx = getIndexForProbability(Config.getRNG().nextDouble() * probabilities(probabilities.length - 1), probabilities)
      individuals(idx)
    }).toList
  }


  private def computeProbabilities(individuals: List[Solution]): Array[Double] = {
    val card = individuals.length
    val prob: List[Double] = individuals map ((s: Solution) => {
        Math.pow((card - s.getFitness() + 1).asInstanceOf[Double] / card, Config.GAMMA) -
                Math.pow((card - s.getFitness()).asInstanceOf[Double] / card, Config.GAMMA)
      }
    )
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
