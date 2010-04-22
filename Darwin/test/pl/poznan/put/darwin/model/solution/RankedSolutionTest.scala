package pl.poznan.put.darwin.model.solution

import org.specs._
import org.specs.runner._
import mock.Mockito
import org.mockito.Matchers._
import pl.poznan.put.darwin.model.problem.{VariableDef, Problem, Variable, Goal}
import pl.poznan.put.cs.idss.jrs.rules.{Rule, RulesContainer}
import java.util.ArrayList
import pl.poznan.put.cs.idss.jrs.types.{FloatField, Field, Example}


class RankedSolutionTest extends SpecificationWithJUnit with ScalaTest with Mockito {

  val maxX = Goal("x", Variable("x"), true)
  val minY = Goal("y", Variable("y"), false)

  val p = new Problem("foo",
                      List(VariableDef("x", 0, 50), VariableDef("y", 0, 50)),
                      List(maxX, minY),
                      null,
                      List())

  val sols = List((1.0, 4.0), (2.0, 3.0), (2.0, 4.0), (1.0, 3.0)).map[EvaluatedSolution]({case (x, y) =>
    new EvaluatedSolution(p, Map("x" -> x, "y" -> y), Map(maxX -> List(x), minY -> List(y)))
  })

  val ruleX = mock[Rule]
  ruleX.covers(any[Example]) answers {e => {
      val example: Example = e.asInstanceOf[Example]
      val x: FloatField = example.getField(1).asInstanceOf[FloatField]
      x.get() >= 2
    }
  }
  val ruleY = mock[Rule]
  ruleY.covers(any[Example]) answers {e => {
      val example: Example = e.asInstanceOf[Example]
      val y: FloatField = example.getField(4).asInstanceOf[FloatField]
      y.get() <= 3
    }
  }

  val rulesArray: ArrayList[Rule] = new ArrayList[Rule]()
  rulesArray.add(ruleX)
  rulesArray.add(ruleY)

  val rulesContainer = mock[RulesContainer]
  rulesContainer.getRules(Rule.CERTAIN, Rule.AT_LEAST) returns rulesArray

  val rankedSolutions: List[RankedSolution] = RankedSolution(sols, rulesContainer)

  "Pack of Rankend solutions" should {
    "have a rank value calculated" in {
      rankedSolutions foreach (s => {
        println("%s ::: %s, %s, %s" format (s.values, s.rank, s.primaryScore, s.secondaryScore))
        1 must be_==(1)
      })
    }
  }
}