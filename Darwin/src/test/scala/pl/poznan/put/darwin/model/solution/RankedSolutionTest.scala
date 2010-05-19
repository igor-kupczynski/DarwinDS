package pl.poznan.put.darwin.model.solution

import java.util.ArrayList
import org.specs._
import org.specs.mock.Mockito
import pl.poznan.put.cs.idss.jrs.rules.{Rule, RulesContainer}
import pl.poznan.put.cs.idss.jrs.types.{FloatField, Example}
import pl.poznan.put.darwin.ProblemRepository
import pl.poznan.put.darwin.jrsintegration.DarwinRulesContainer
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.{VariableDef, Problem, Variable, Goal}
import pl.poznan.put.darwin.simulation.Simulation


class RankedSolutionTest extends Specification with Mockito with ProblemRepository{

  val maxX = Goal("x", Variable("x"), true)
  val minY = Goal("y", Variable("y"), false)

  val p = new Problem("foo",
                      List(VariableDef("x", 0, 50, null), VariableDef("y", 0, 50, null)),
                      List(maxX, minY),
                      null,
                      List())
  val sim = new Simulation(defaultConfig, p)

  // Two tests to perform
  val sols = List((1.0, 4.0), (2.0, 3.0), (2.0, 4.0), (1.0, 3.0)).map({case (x, y) =>
    new EvaluatedSolution(sim, Map("x" -> x, "y" -> y), Map(maxX -> List(x), minY -> List(y)))
  })
  val sols2 = List((1.0, 4.0), (3.0, 2.0), (6.0, 1.0)).map({case (x, y) =>
    new EvaluatedSolution(sim, Map("x" -> x, "y" -> y), Map(maxX -> List(x), minY -> List(y)))
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

  val rankedSolutions: List[RankedSolution] =
    RankedSolution(sols, DarwinRulesContainer(rulesContainer, sols))
  val rankedSolutions2: List[RankedSolution] =
    RankedSolution(sols2, DarwinRulesContainer(rulesContainer, sols2))

  "Pack of Rankend solutions" should {
    "be in a rank order" in {
      for (idx <- 0 to 3) {
        rankedSolutions(idx).rank must be_==(idx+1)
      }
      for (idx <- 0 to 2) {
        rankedSolutions2(idx).rank must be_==(idx+1)
      }
    }
    "have correctly calculated primary scores" in {
      val sim = rankedSolutions(0).sim
      val pScores1 = List(2 * math.pow(1-sim.config.DELTA, 2),
                          math.pow(1-sim.config.DELTA, 2),
                          math.pow(1-sim.config.DELTA, 2),
                          0)
      for (idx <- 0 to 3) {
        rankedSolutions(idx).primaryScore must be_==(pScores1(idx))
      }
      val sim2 = rankedSolutions2(0).sim
      val pScores2 = List(2 * math.pow(1-sim2.config.DELTA, 2),
                          2 * math.pow(1-sim2.config.DELTA, 2),
                          0)
      for (idx <- 0 to 2) {
        rankedSolutions2(idx).primaryScore must be_==(pScores2(idx))
      }
    }
    "have calulated secondary scores" in {
      for ((sol: RankedSolution) <- rankedSolutions) {
        sol.secondaryScore must be_>(0.0)
      }
      val sScores = List(Double.MaxValue, 24.0, Double.MaxValue) // Yeap, 24.
                                                                 // Because of
                                                                 // Percentile space
      for (idx <- 0 to 2) {
        rankedSolutions2(idx).secondaryScore must be_==(sScores(idx))
      }
    }
  }
}
