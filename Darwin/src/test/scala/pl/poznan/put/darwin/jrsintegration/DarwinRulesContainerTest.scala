package pl.poznan.put.darwin.jrsintegration

import org.specs.mock.Mockito
import org.specs.runner.ScalaTest
import pl.poznan.put.cs.idss.jrs.rules.{RulesContainer, Rule}
import pl.poznan.put.cs.idss.jrs.types.{Example, FloatField}
import java.util.ArrayList
import pl.poznan.put.darwin.model.solution.EvaluatedSolution
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.{Goal, Variable, Problem, VariableDef}
import org.specs.SpecificationWithJUnit

class DarwinRulesContainerTest extends SpecificationWithJUnit with ScalaTest with Mockito {

  "DarwinRulesContainer" should {
    val drc = DarwinRulesContainer(mockJrsIntegration(), sampleSolutions())
    "rember rules generated by JRS" in {
      drc.rules.length must be_==(2)
    }
    "calculate weights correctly" in {
      drc.rules(0)._2 must be_==(Math.pow(1 - Config.DELTA, 2))
      drc.rules(1)._2 must be_==(Math.pow(1 - Config.DELTA, 1))
    }
  }

  def sampleSolutions(): List[EvaluatedSolution] = {
    val maxX = Goal("x", Variable("x"), true)
    val minY = Goal("y", Variable("y"), false)
    val p = new Problem("foo",
                      List(VariableDef("x", 0, 50), VariableDef("y", 0, 50)),
                      List(maxX, minY),
                      null,
                      List())

    List((1.0, 4.0), (3.0, 4.0), (6.0, 1.0)).map[EvaluatedSolution]({case (x, y) =>
      new EvaluatedSolution(p, Map("x" -> x, "y" -> y), Map(maxX -> List(x), minY -> List(y)))
    })
  }

  def mockJrsIntegration(): RulesContainer = {
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
    rulesContainer
  }
}