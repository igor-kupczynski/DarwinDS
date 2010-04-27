package pl.poznan.put.darwin.model

import java.util.Random
import org.ini4j.ConfigParser
import pl.poznan.put.cs.idss.jrs.rules.VCDomLem

class Config(parser: ConfigParser) {

  println(parser.sections())
  println(parser.options("main"))
  /*
   * Main parameters
   */
  val SOLUTION_COUNT: Int = parser.getInt("main", "solutioncount")
  val SCENARIO_COUNT: Int = parser.getInt("main", "scenariocount")
  val PERCENTILES: List[Int] = 1 :: 25 :: 50 :: Nil;
  val DELTA: Double = parser.getDouble("main", "delta")
  val GAMMA: Double = parser.getDouble("main", "gamma")
  val MUTATION_TRIES: Int = parser.getInt("main", "mutationtries")
  val ETA: Double = parser.getDouble("main", "eta")
  val OMEGA: Double = parser.getDouble("main", "omega")
  val USE_AVG: Boolean = parser.getBoolean("main", "useavg")

  /*
   * Mocked DM options
   */
  val GOOD_COUNT: Int = parser.getInt("mockeddm", "goodcount")
  
  /*
   * DOMLEM CONFIG
   */
  val DOMLEM_CONFIDECE_LEVEL = 1.0
  val CONDITION_SELECTION_METHOD = VCDomLem.MIX_CONDITIONS_FROM_DIFFERENT_OBJECTS
  val NEGATIVE_EXAMPLES_TREATMENT = VCDomLem.COVER_NONE_OF_NEGATIVE_EXAMPLES
}


