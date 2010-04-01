package pl.poznan.put.darwin.model.problem

class ParserTest {

  import org.junit._, Assert._

  @Test def parserTest = {
    val exprs = "var[0, 10] x1;var [0,15] x2 ;" ::
    "var[-1.5, 1.5] x1; var[-100, 200.13] x2; min time: x1 + x2; max profit: min(x1 + x2, x1*-x2); limit: x1 <= -10;" :: Nil

    exprs foreach (e => {
      val res = Parser.ProblemParser.parse(e)
      println(res)
      assertEquals(true, res.successful)
    })
  }
}