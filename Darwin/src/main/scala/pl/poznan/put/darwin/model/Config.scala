package pl.poznan.put.darwin.model

import java.util.Random
import org.ini4j.ConfigParser
import scala.util.matching.Regex
import pl.poznan.put.cs.idss.jrs.rules.VCDomLem
import java.io.ByteArrayInputStream

object Config {

  def apply(): Config = new Config(Config.parser)

  def parser: ConfigParser = {
    val defConf = """
    [main]
    debug = false
    solutionCount = 30
    scenarioCount = 30
    generationCount = 30
    outerCount = 10
    delta = 0.1
    gamma = 2.0
    eta = 0.5
    omega = 0.1
    useAvg = false
    useAtMost = false
    mutationTries = 100
    percentiles = 1, 25, 50
    multiRules = false
    multiRulesCount = 3

    [mockedDM]
    baseGoodCount = 3
    goodCountDelta = 0
    noiseLevel = 0

    [algo]
    allrules = false
    domlemconfidenceLevel = 1.0

    [reports]
    evolutionReport = reports/evolution_report.csv
    DMReport = reports/dm_report.csv
    briefReport = false
    rulesDirectory = rules

    [evolution]
    regenerateEvery = 1000
    regeneratePercent = 0.0
    compareusingsupposedutility = False

    [gui]
    digits = 2
    """
    val parser = new ConfigParser()
    parser.read(new ByteArrayInputStream(defConf.getBytes("UTF-8")))
    parser
  }

  def preconfParser(preconf: Config): ConfigParser = {
    val p = parser
    p.set("main", "debug", preconf.DEBUG)
    p.set("main", "solutioncount", preconf.SOLUTION_COUNT)
    p.set("main", "scenariocount", preconf.SCENARIO_COUNT)
    p.set("main", "generationcount", preconf.GENERATION_COUNT)
    p.set("main", "outercount", preconf.OUTER_COUNT)
    p.set("main", "percentiles", preconf.PERCENTILES.mkString(", "))
    p.set("main", "delta", preconf.DELTA)
    p.set("main", "gamma", preconf.GAMMA)
    p.set("main", "mutationtries", preconf.MUTATION_TRIES)
    p.set("main", "eta", preconf.ETA)
    p.set("main", "omega", preconf.OMEGA)
    p.set("main", "useavg", preconf.USE_AVG)
    p.set("main", "useatmost", preconf.USE_AT_MOST)
    p.set("main", "multirules", preconf.MULTI_RULES)
    p.set("main", "multirulescount", preconf.MULTI_RULES_COUNT)
    p.set("mockeddm", "basegoodcount", preconf.BASE_GOOD_COUNT)
    p.set("mockeddm", "goodcountdelta", preconf.GOOD_COUNT_DELTA)
    p.set("mockeddm", "noiselevel", preconf.NOISE_LEVEL)
    p.set("algo", "allrules", preconf.ALL_RULES)
    p.set("algo", "domlemconfidencelevel", preconf.DOMLEM_CONFIDECE_LEVEL)
    p.set("reports", "evolutionreport", preconf.EVOLUTION_REPORT)
    p.set("reports", "dmreport", preconf.DM_REPORT)
    p.set("reports", "briefreport", preconf.BRIEF_REPORT)
    p.set("reports", "rulesdirectory", preconf.RULES_DIRECTORY)
    p.set("evolution", "regenerateevery", preconf.REGENERATE_SCENARIONS_EVERY_GENERATIONS)
    p.set("evolution", "regeneratepercent", preconf.REGENERATE_PERCENT_OF_SCENARIONS)
    p.set("evolution", "compareusingsupposedutility", preconf.COMPARE_USING_SUPPOSED_UTILITY)
    p.set("gui", "digits", preconf.DIGITS_AFTER_DOT)
    p
  }
}

class Config(parser: ConfigParser) {

  var problem_name: String = "problem"

  /*
   * Main parameters
   */
  val DEBUG: Boolean = parser.getBoolean("main", "debug")

  val SOLUTION_COUNT: Int = parser.getInt("main", "solutioncount")
  val SCENARIO_COUNT: Int = parser.getInt("main", "scenariocount")
  val GENERATION_COUNT: Int = parser.getInt("main", "generationcount")
  val OUTER_COUNT: Int  = parser.getInt("main", "outercount")
  val PERCENTILES: List[Int] = getListOfInts(parser.get("main", "percentiles"))
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
  val RULES_DIRECTORY = parser.get("reports", "rulesdirectory")


  /*
   * Evolution options
   */
  val REGENERATE_SCENARIONS_EVERY_GENERATIONS: Int =
    parser.getInt("evolution", "regenerateevery")
  val REGENERATE_PERCENT_OF_SCENARIONS: Double =
    parser.getDouble("evolution", "regeneratepercent")
  val COMPARE_USING_SUPPOSED_UTILITY: Boolean =
    parser.getBoolean("evolution", "compareusingsupposedutility")


  /*
   * Gui options
   */
  val DIGITS_AFTER_DOT = if (parser.hasOption("gui", "digits"))
     parser.getInt("gui", "digits") else 2

  private def getListOfInts(str: String): List[Int] = {
    val r = new Regex(",")
    r.split(str).toList.map(x => x.trim.toInt)
  }
  
}


