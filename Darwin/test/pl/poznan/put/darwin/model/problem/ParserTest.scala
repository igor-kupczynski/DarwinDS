package pl.poznan.put.darwin.model.problem

class ParserTest {

  import org.junit._, Assert._

  @Test def parserTest = {
    val exprs = "var[0, 10] x1;var [0,15] x2 ;" ::
    "var[-1.5, 1.5] x1; var[-100, 200.13] x2; min time: x1 + x2; max profit: min(x1 + x2, x1*-x2); limit: x1 <= -10;" ::
    "var[0,10] x1; var[0,5] x2;\nmin time: x1 + 2*x2;\nmax profit: x1 + 3*x2;\nmarket_limit_1: x1 <= 10;\nmarket_limit_2: x2 <= 5;\nmaterial_limit_3: x1 + 2*x2 <= 15;" ::
    Nil


    exprs foreach (e => {
      val res = Parser.ProblemParser.parse(e)
      println(res)
      assertEquals(true, res.successful)
    })
  }
}