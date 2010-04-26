package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.problem.{Problem, Parser}

/**
* Base trait for unittesting some components of Darwin
*
* @author Igor Kupczynski
*/
trait ProblemRepository {

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

  val simpleWithIntervals: Problem = Parser.ProblemParser.parse("""
      var[0.0, 200.0] x;
      max profit: x;
      !dec: profit;
      nonZero: x >= 0.0;
      limit: ([i1: 0.9, 1.1] * x) <= 100.0;
      """).get
  
  val simpleNoIntervals: Problem = Parser.ProblemParser.parse(
      "var[0.0, 200.0] x;\n\n" +
      "max profit: x;\n\n" +
      "!dec: profit;\n\n" +
      "nonZero: x >= 0.0;\n" +
      "limit: x <= 100.0;\n"
  ).get

}
