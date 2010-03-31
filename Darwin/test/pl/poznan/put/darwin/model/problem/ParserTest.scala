package pl.poznan.put.darwin.model.problem

class ParserTest {

  import org.junit._, Assert._

  @Test def parserTest = {
    val exprs = "var x1;var x2 ;" ::
    "var x1; var x2; min time: x1 + x2; max profit: min(x1 + x2, x1*x2); limit: x1 <= 10;" :: Nil

    exprs foreach (e => {
      val res = Parser.ProblemParser.parse(e)
      assertEquals(true, res.successful)
    })
  }
}