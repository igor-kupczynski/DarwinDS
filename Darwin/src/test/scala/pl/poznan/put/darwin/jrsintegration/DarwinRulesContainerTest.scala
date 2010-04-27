package pl.poznan.put.darwin.jrsintegration

import java.util.ArrayList
import org.specs.Specification
import org.specs.mock.Mockito
import pl.poznan.put.cs.idss.jrs.rules.{RulesContainer, Rule}
import pl.poznan.put.cs.idss.jrs.types.{Example, FloatField}
import pl.poznan.put.darwin.ProblemRepository
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.{Goal, Variable, Problem, VariableDef}
import pl.poznan.put.darwin.model.solution.EvaluatedSolution
import pl.poznan.put.darwin.simulation.Simulation

class DarwinRulesContainerTest extends Specification with Mockito with ProblemRepository {

  "DarwinRulesContainer" should {
    val ss = sampleSolutionsForContainer()
    val drc = DarwinRulesContainer(mockJrsIntegration(), ss)
    "rember rules generated by JRS" in {
      drc.rules.length must be_==(2)
    }
    "calculate weights correctly" in {
      drc.rules(0)._2 must be_==(math.pow(1 - ss(0).sim.config.DELTA, 2))
      drc.rules(1)._2 must be_==(math.pow(1 - ss(0).sim.config.DELTA, 1))
    }
    "calculate score for evaluated solutions correctly" in {
      val sols = solutionsToCheckScores
      sols foreach { case (sol, v) => drc.getScore(sol) must be_==(v) }
      sols.length must be_==(4)    
    }
  }

  def solutionsToCheckScores(): List[Tuple2[EvaluatedSolution, Double]] = {
    val sols = sampleSolutions(List((1.0, 4.0),
                                    (1.0, 1.0),
                                    (4.0, 4.0),
                                    (4.0, 1.0)))
    val exp = List(0,
                   math.pow(1 - sols(0).sim.config.DELTA, 1),
                   math.pow(1 - sols(0).sim.config.DELTA, 2),
                   math.pow(1 - sols(0).sim.config.DELTA, 1) +
                     math.pow(1 - sols(0).sim.config.DELTA, 2))
    sols.zip(exp)
                   
  }
    

  def sampleSolutionsForContainer(): List[EvaluatedSolution] =
    sampleSolutions(List((1.0, 4.0), (3.0, 4.0), (6.0, 1.0)))
  
  def sampleSolutions(points: List[Tuple2[Double, Double]]): List[EvaluatedSolution] = {
    val maxX = Goal("x", Variable("x"), true)
    val minY = Goal("y", Variable("y"), false)
    val p = new Problem("foo",
                        List(VariableDef("x", 0, 50), VariableDef("y", 0, 50)),
                        List(maxX, minY),
                        null,
                        List())
    val sim = new Simulation(defaultConfig, p)
    points.map({case (x, y) =>
      new EvaluatedSolution(sim, Map("x" -> x, "y" -> y), Map(maxX -> List(x), minY -> List(y)))
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
