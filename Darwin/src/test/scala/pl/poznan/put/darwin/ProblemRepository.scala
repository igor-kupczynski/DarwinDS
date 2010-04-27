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
  delta = 0.1
  gamma = 2.0
  eta = 0.5
  omega = 0.1
  useAvg = false
  mutationTries = 100
  percentiles = 1.0, 25.0, 50.0

  [mockedDM]
  goodCount = 3
  """
  val parser = new ConfigParser()
  parser.read(new ByteArrayInputStream(defConf.getBytes("UTF-8")))
  val defaultConfig = new Config(parser)
  
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
      "!dec: profit;\n\n" +
      "nonZero: x >= 0.0;\n" +
      "limit: x <= 100.0;\n"
  ).get
  val simpleNoIntervalsSim = new Simulation(defaultConfig,
                                            simpleNoIntervals)
}
