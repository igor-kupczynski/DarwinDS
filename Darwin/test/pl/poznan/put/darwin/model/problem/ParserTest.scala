package pl.poznan.put.darwin.model.problem

class ParserTest {

  import org.junit._, Assert._

  @Test def parserTest = {
    val exprs = "var[0, 10] x1;var [0,15] x2 ;" ::
    "var[-1.5, 1.5] x1; var[-100, 200.13] x2; min time: x1 + x2; max profit: min(x1 + x2, x1*-x2); limit: x1 <= -10;" ::
    "var[0,10] x1; var[0,5] x2;\nmin time: x1 + 2*x2;\nmax profit: x1 + 3*x2;\nmarket_limit_1: x1 <= 10;\nmarket_limit_2: x2 <= 5;\nmaterial_limit_3: x1 + 2*x2 <= 15;" ::
    Nil

    val toStringRes = "var[0.0, 10.0] x1;\nvar [0.0,15.0] x2;\n" ::
    ("var[-1.5, 1.5] x1;\n" +
    "var[-100.0, 200.13] x2;\n\n" +
    "min time: (x1 + x2);\n" +
    "max profit: min((x1 + x2), (x1 * -(x2)));\n\n" +
    "limit: x1 <= -10.0;\n") ::
    ("var[0.0, 10.0] x1;\n" +
    "var[0.0, 5.0] x2;\n\n" +
    "min time: (x1 + (2.0 * x2));\n" +
    "max profit: (x1 + (3.0 * x2));\n\n" +
    "market_limit_1: x1 <= 10.0;\n" +
    "market_limit_2: x2 <= 5.0;\n" +
    "material_limit_3: (x1 + (2.0 * x2)) <= 15.0;\n") ::
    Nil

    for (idx <- 1 to (exprs.length-1)) {
      val e = exprs(idx)
      val res = Parser.ProblemParser.parse(e)
      assertEquals(true, res.successful)
      assertEquals(toStringRes(idx), res.get.toString)
    }
  }
}