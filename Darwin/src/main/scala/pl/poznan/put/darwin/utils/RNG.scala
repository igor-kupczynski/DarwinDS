package pl.poznan.put.darwin.utils

import java.util.Random
  
/**
 * Object granting access to random number generator
 *
 * @author: Igor Kupczynski
 */
 
object RNG {

  private val rng: Random = new Random()
  def get(): Random = {
    rng
  }
  
}
