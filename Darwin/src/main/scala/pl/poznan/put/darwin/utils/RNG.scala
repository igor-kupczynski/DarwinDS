package pl.poznan.put.darwin.utils

import java.util.Random
  
/**
 * Object granting access to random number generator
 *
 * @author: Igor Kupczynski
 */
 
object RNG {

  private val rng: RNG = new RNG()
  
  def apply(): RNG = {
    rng
  }

  
}

/** 
 * Modified RNG interface
 */
class RNG {

  private val rng: Random = new Random()

  def nextBoolean() = {
    rng.nextBoolean()
  }

  def nextGaussian() = {
    rng.nextGaussian()
  }
  
  def nextDouble() = {
    rng.nextDouble()
  }

  def nextInt(x: Int) = {
    rng.nextInt(x)
  }
  
  def nextDoubleInclusive(): Double = {
    var result: Double = 0.0
    do {
      result = rng.nextDouble() * 1.1
    } while (result > 1.0)
    result
  }
}
