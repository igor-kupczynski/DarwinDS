package pl.poznan.put.darwin.model.solution

import org.specs._
import mock.Mockito
import org.mockito.Matchers._
import pl.poznan.put.darwin.model.problem.{VariableDef, Problem, Variable, Goal}
import pl.poznan.put.cs.idss.jrs.rules.{Rule, RulesContainer}
import pl.poznan.put.cs.idss.jrs.types.Example
import java.util.ArrayList

class RankedSolutionTest extends SpecificationWithJUnit with Mockito {

  val maxX = Goal("x", Variable("x"), true)
  val minY = Goal("y", Variable("y"), false)

  val p = new Problem("foo",
                      List(VariableDef("x", 0, 50), VariableDef("y", 0, 50)),
                      List(maxX, minY),
                      null,
                      List())

  val sols = List((5.0, 20.0), (10.0, 15.0), (10.0, 20.0), (5.0, 15.0)).map[EvaluatedSolution]({case (x, y) => 
    new EvaluatedSolution(p, Map("x" -> x, "y" -> y), Map(maxX -> List(x), minY -> List(y)))
  })

  val ruleX: Rule = mock(Rule)
  ruleX.covers(any[Example]) answers {e =>
    println e
    true
  }
  val ruleY: Rule = mock(Rule)
  ruleY.covers(any[Example]) answers {e =>
    println(e)
    true
  }
  val rules = mock(ArrayList)
  rules.toArray returns Array[Rule](ruleX, ruleY)

  val rulesContainer = mock(RulesContainer)
  rulesContainer.getRules(Rule.CERTAIN, Rule.AT_LEAST) returns rules

  val rankedSolutions: List[RankedSolution] = RankedSolution(sols, rulesContainer)

}