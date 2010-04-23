package pl.poznan.put.darwin.evolution

import pl.poznan.put.darwin.model.problem.{Problem, Parser}

/**
* Base trait for unittesting evolutionary part of Darwin
*
* @author Igor Kupczynski
*/
trait BaseEvolution {

  def trainsSoldiersNoIntervals: Problem = Parser.ProblemParser.parse(
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

}