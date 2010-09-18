package pl.poznan.put.darwin.model

import java.util.Random
import org.ini4j.ConfigParser
import scala.util.matching.Regex
import pl.poznan.put.cs.idss.jrs.rules.VCDomLem

class Config(parser: ConfigParser) {

  /*
   * Main parameters
   */
  val DEBUG: Boolean = parser.getBoolean("main", "debug")


  val SOLUTION_COUNT: Int = parser.getInt("main", "solutioncount")
  val SCENARIO_COUNT: Int = parser.getInt("main", "scenariocount")
  val GENERATION_COUNT: Int = parser.getInt("main", "generationcount")
  val OUTER_COUNT: Int  = parser.getInt("main", "outercount")
  val PERCENTILES: List[Double] = getListOfDoubles(parser.get("main", "percentiles"))
  val DELTA: Double = parser.getDouble("main", "delta")
  val GAMMA: Double = parser.getDouble("main", "gamma")
  val MUTATION_TRIES: Int = parser.getInt("main", "mutationtries")
  val ETA: Double = parser.getDouble("main", "eta")
  val OMEGA: Double = parser.getDouble("main", "omega")

  val USE_AVG: Boolean = parser.getBoolean("main", "useavg")
  val USE_AT_MOST: Boolean = parser.getBoolean("main", "useatmost")

  val MULTI_RULES: Boolean = parser.getBoolean("main", "multirules")
  val MULTI_RULES_COUNT: Int = parser.getInt("main", "multirulescount")

  /*
   * Mocked DM options
   */
  val BASE_GOOD_COUNT: Int = parser.getInt("mockeddm", "basegoodcount")
  val GOOD_COUNT_DELTA: Int = parser.getInt("mockeddm", "goodcountdelta")
  val NOISE_LEVEL: Int = parser.getInt("mockeddm", "noiselevel")

  /*
   * ALGO CONFIG
   */
  val ALL_RULES = parser.getBoolean("algo", "allrules")
  val DOMLEM_CONFIDECE_LEVEL = parser.getDouble("algo", "domlemconfidencelevel")
  val CONDITION_SELECTION_METHOD = VCDomLem.MIX_CONDITIONS_FROM_DIFFERENT_OBJECTS
  val NEGATIVE_EXAMPLES_TREATMENT = VCDomLem.COVER_ONLY_INCONSISTENT_AND_BOUNDARY_NEGATIVE_EXAMPLES


  /*
   * Report options
   */
  val EVOLUTION_REPORT = parser.get("reports", "evolutionreport")
  val DM_REPORT = parser.get("reports", "dmreport")
  val BRIEF_REPORT = parser.getBoolean("reports", "briefreport")


  /*
   * Evolution options
   */
  val REGENERATE_SCENARIONS_EVERY_GENERATIONS: Int =
    parser.getInt("evolution", "regenerateevery")
  val REGENERATE_PERCENT_OF_SCENARIONS: Double =
    parser.getDouble("evolution", "regeneratepercent")
  val COMPARE_USING_SUPPOSED_UTILITY: Boolean =
    parser.getBoolean("evolution", "compareusingsupposedutility")
  
  private def getListOfDoubles(str: String): List[Double] = {
    val r = new Regex(",")
    r.split(str).toList.map(x => x.toDouble)
  }
}


