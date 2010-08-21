package pl.poznan.put.darwin

import pl.poznan.put.darwin.model.problem.{Problem, Parser}
import pl.poznan.put.darwin.simulation.Simulation
import pl.poznan.put.darwin.model.Config
import java.io.ByteArrayInputStream;
import org.ini4j.ConfigParser

/**
* Base trait for unittesting some components of Darwin
*
* @author Igor Kupczynski
*/
trait ProblemRepository {

  val defConf = """
  [main]
  solutionCount = 30
  scenarioCount = 30
  generationCount = 60
  outerCount = 10
  delta = 0.1
  gamma = 2.0
  eta = 0.5
  omega = 0.1
  useAvg = false
  useAtMost = false
  mutationTries = 100
  percentiles = 1.0, 25.0, 50.0
  multiRules = false
  multiRulesCount = 3
  
  [mockedDM]
  baseGoodCount = 3
  goodCountDelta = 0
  noiseLevel = 0

  [DomLem]
  confidenceLevel = 1.0
  
  [reports]
  evolutionReport = out/evolution_report.csv
  DMReport = out/dm_report.csv
  briefReport = true

  [evolution]
  regenerateEvery = 1000
  regeneratePercent = 0.0
  """
  val parser = new ConfigParser()
  parser.read(new ByteArrayInputStream(defConf.getBytes("UTF-8")))
  val defaultConfig = new Config(parser)

  val confWithAvg = """
  [main]
  solutionCount = 30
  scenarioCount = 30
  generationCount = 60
  outerCount = 10
  delta = 0.1
  gamma = 2.0
  eta = 0.5
  omega = 0.1
  useAvg = true
  useAtMost = false
  mutationTries = 100
  percentiles = 1.0, 25.0, 50.0
  multiRules = false
  multiRulesCount = 3
  
  [mockedDM]
  baseGoodCount = 3
  goodCountDelta = 0
  noiseLevel = 0

  [DomLem]
  confidenceLevel = 1.0

  [reports]
  evolutionReport = out/evolution_report.csv
  DMReport = out/dm_report.csv
  briefReport = true

  [evolution]
  regenerateEvery = 1000
  regeneratePercent = 0.0
  """
  val parserWithAvg = new ConfigParser()
  parserWithAvg.read(new ByteArrayInputStream(confWithAvg.getBytes("UTF-8")))
  val configWithAvg = new Config(parserWithAvg)
  
  val trainsSoldiersNoIntervals: Problem = Parser.ProblemParser.parse(
    "var[0,200] x1;\n" +
    "var[0,200] x2;\n" +
    "max z: 3*x1 + 2*x2;\n" +
    "!dec: z;\n" +
    "Finishing: 2*x1 + x2 <= 100;\n" +
    "Carpentr: x1 + x2 <= 80;\n" +
    "Demand: x1 <= 40;\n" +
    "nonZero1: x1 >= 0;\n" +
    "nonZero2: x2 >= 0;\n"
  ).get
  val trainsSoldiersNoIntervalsSim = new Simulation(defaultConfig,
                                                    trainsSoldiersNoIntervals)


  val integerTrainsSoldiersNoIntervals: Problem = Parser.ProblemParser.parse(
    "var[(I) 0,200] x1;\n" +
    "var[(I) 0,200] x2;\n" +
    "max z: 3*x1 + 2*x2;\n" +
    "!dec: z;\n" +
    "Finishing: 2*x1 + x2 <= 100;\n" +
    "Carpentr: x1 + x2 <= 80;\n" +
    "Demand: x1 <= 40;\n" +
    "nonZero1: x1 >= 0;\n" +
    "nonZero2: x2 >= 0;\n"
  ).get
  val integerTrainsSoldiersNoIntervalsSim = new Simulation(defaultConfig,
                                                    integerTrainsSoldiersNoIntervals)
  
  val simpleWithIntervals: Problem = Parser.ProblemParser.parse("""
      var[0.0, 200.0] x;
      max profit: x;
      !dec: profit;
      nonZero: x >= 0.0;
      limit: ([i1: 0.9, 1.1] * x) <= 100.0;
      """).get
  val simpleWithIntervalsSim = new Simulation(defaultConfig,
                                              simpleWithIntervals)

  
  val simpleNoIntervals: Problem = Parser.ProblemParser.parse(
      "var[0.0, 200.0] x;\n\n" +
      "max profit: x;\n\n" +
      "!dec: profit(0.10) + profit(0.25);\n\n" +
      "nonZero: x >= 0.0;\n" +
      "limit: x <= 100.0;\n"
  ).get
  val simpleNoIntervalsSim = new Simulation(defaultConfig,
                                            simpleNoIntervals)

  val binarySimpleNoIntervals: Problem = Parser.ProblemParser.parse(
      "var[(B)0.0, 200.0] x;\n\n" +
      "max profit: x;\n\n" +
      "!dec: profit;\n\n" +
      "nonZero: x >= 0.0;\n" +
      "limit: x <= 100.0;\n"
  ).get
  val binarySimpleNoIntervalsSim = new Simulation(defaultConfig,
                                                  binarySimpleNoIntervals)

  
}
