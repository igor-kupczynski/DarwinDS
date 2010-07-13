package pl.poznan.put.darwin.model.problem

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite

class ParserTest extends JUnitSuite {

  @Test def parserTest = {
    val exprs = ("var[0, 10] x1;var [0,15] x2 ;") ::
    ("var[-1.5, 1.5] x1; var[-100, 200.13] x2; min time: x1 + x2;\n" +
     "max profit: min(x1 + x2, x1*-x2); limit: x1 <= -10;") ::
    ("var[-1.5, 1.5] x1; var[-100, 200.13] x2; min time: sum(x1, x2, x1 * x2);\n" +
     "max profit: min(x1 + x2, x1*-x2); limit: x1 <= -10;") ::
    ("var[0,10] x1; var[0,5] x2;\nmin time: x1 + 2*x2;\n" +
     "max profit: x1 + 3*x2;\nmarket_limit_1: x1 <= 10;\n" +
     "market_limit_2: x2 <= 5;\nmaterial_limit_3: x1 + 2*x2 <= 15;") ::
    ("var[0,200] x1;\nvar[0,200] x2;\nmax z: 3*x1 + 2*x2;\n" +
     "!dec: z;Finishing: 2*x1 + x2 <= 100;\nCarpentr: x1 + x2 <= 80;\n" +
     "Demand: x1 <= 40;\nnonZero1: x1 >= 0;\nnonZero2: x2 >= 0;") ::
    ("var[0, 10.0] x;\n" +
     "/* multilne \n silly comment */\n" +
     "# Just a silly comment\n" +
     "max profit/*Another silly comment*/: [i1: 20, 40] * x;") ::
    ("var[(B)0,10] x1; var[ (I) 0,5] x2;\nmin time: x1 + 2*x2;\n" +
     "max profit: x1 + 3*x2;\nmarket_limit_1: x1 <= 10;\n" +
     "market_limit_2: x2 <= 5;\nmaterial_limit_3: x1 + 2*x2 <= 15;") ::
    ("""var[(B) 0.0, 2.0] x0;
     var[(B) 0.0, 2.0] x1;
     var[(B) 0.0, 2.0] x2;
     var[(B) 0.0, 2.0] x3;
     var[(B) 0.0, 2.0] x4;
     var[(B) 0.0, 2.0] x5;
     var[(B) 0.0, 2.0] x6;
     var[(B) 0.0, 2.0] x7;
     var[(B) 0.0, 2.0] x8;
     var[(B) 0.0, 2.0] x9;

     max value1: sum(2.120660 * x0, 8.205259 * x1, 3.660497 * x2, 3.212720 * x3, 3.661733 * x4, 5.522523 * x5, 7.740060 * x6, 7.125582 * x7, 7.123816 * x8, 3.185270 * x9);
     max value2: sum(6.657414 * x0, 1.982796 * x1, 2.245550 * x2, 8.689134 * x3, 9.988014 * x4, 1.819508 * x5, 4.888839 * x6, 8.462258 * x7, 5.968954 * x8, 4.585634 * x9);
     weight: sum(ln(8.103435) * x0, 4.069466 * x1, 8.993478 * x2, 1.203721 * x3, 9.941009 * x4, 5.631480 * x5, 2.534965 * x6, 2.365333 * x7, 3.056016 * x8, 8.636533 * x9) <= 13.633859;

     !dec: 1.000000 * value1 + 2.000000 * value2;
     """) :: Nil

    val toStringRes = "var[0.0, 10.0] x1;\nvar [0.0,15.0] x2;\n" ::
    ("var[-1.5, 1.5] x1;\n" +
     "var[-100.0, 200.13] x2;\n\n" +
     "min time: (x1 + x2);\n" +
     "max profit: min((x1 + x2), (x1 * -(x2)));\n\n" +
     "limit: x1 <= -10.0;\n") ::
    ("var[-1.5, 1.5] x1;\n" +
     "var[-100.0, 200.13] x2;\n\n" +
     "min time: sum(x1, x2, (x1 * x2));\n" +
     "max profit: min((x1 + x2), (x1 * -(x2)));\n\n" +
     "limit: x1 <= -10.0;\n") ::
    ("var[0.0, 10.0] x1;\n" +
     "var[0.0, 5.0] x2;\n\n" +
     "min time: (x1 + (2.0 * x2));\n" +
     "max profit: (x1 + (3.0 * x2));\n\n" +
     "market_limit_1: x1 <= 10.0;\n" +
     "market_limit_2: x2 <= 5.0;\n" +
     "material_limit_3: (x1 + (2.0 * x2)) <= 15.0;\n") ::
    ("var[0.0, 200.0] x1;\n" +
     "var[0.0, 200.0] x2;\n\n" +
     "max z: ((3.0 * x1) + (2.0 * x2));\n\n" +
     "!dec: z;\n\n" +
     "Finishing: ((2.0 * x1) + x2) <= 100.0;\n" +
     "Carpentr: (x1 + x2) <= 80.0;\n" +
     "Demand: x1 <= 40.0;\n" +
     "nonZero1: x1 >= 0.0;\n" +
     "nonZero2: x2 >= 0.0;\n" ) ::
    ("var[0.0, 10.0] x;\n\n" +
     "max profit: ([i1: 20.0, 40.0] * x);\n\n") ::
    ("var[(B) 0.0, 10.0] x1;\n" +
     "var[(I) 0.0, 5.0] x2;\n\n" +
     "min time: (x1 + (2.0 * x2));\n" +
     "max profit: (x1 + (3.0 * x2));\n\n" +
     "market_limit_1: x1 <= 10.0;\n" +
     "market_limit_2: x2 <= 5.0;\n" +
     "material_limit_3: (x1 + (2.0 * x2)) <= 15.0;\n") ::
    ("""var[(B) 0.0, 2.0] x0;
var[(B) 0.0, 2.0] x1;
var[(B) 0.0, 2.0] x2;
var[(B) 0.0, 2.0] x3;
var[(B) 0.0, 2.0] x4;
var[(B) 0.0, 2.0] x5;
var[(B) 0.0, 2.0] x6;
var[(B) 0.0, 2.0] x7;
var[(B) 0.0, 2.0] x8;
var[(B) 0.0, 2.0] x9;

max value1: sum((2.12066 * x0), (8.205259 * x1), (3.660497 * x2), (3.21272 * x3), (3.661733 * x4), (5.522523 * x5), (7.74006 * x6), (7.125582 * x7), (7.123816 * x8), (3.18527 * x9));
max value2: sum((6.657414 * x0), (1.982796 * x1), (2.24555 * x2), (8.689134 * x3), (9.988014 * x4), (1.819508 * x5), (4.888839 * x6), (8.462258 * x7), (5.968954 * x8), (4.585634 * x9));

!dec: ((1.0 * value1) + (2.0 * value2));

weight: sum((ln(8.103435) * x0), (4.069466 * x1), (8.993478 * x2), (1.203721 * x3), (9.941009 * x4), (5.63148 * x5), (2.534965 * x6), (2.365333 * x7), (3.056016 * x8), (8.636533 * x9)) <= 13.633859;
""") ::
    Nil

    for (idx <- 1 to (exprs.length-1)) {
      val e = exprs(idx)
      val res = Parser.ProblemParser.parse(e)
      if (res.successful == false) {
        println(res)
      }
      assertEquals(true, res.successful)
      assertEquals(toStringRes(idx), res.get.toString)
    }
  }
}
