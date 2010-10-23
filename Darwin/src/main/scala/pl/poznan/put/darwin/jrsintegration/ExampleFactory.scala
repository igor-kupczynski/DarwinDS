package pl.poznan.put.darwin.jrsintegration

import pl.poznan.put.darwin.model.problem.Goal
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.solution.{EvaluatedSolution, MarkedSolution}
import pl.poznan.put.cs.idss.jrs.types._

/**
 * Object converting solution to jrs example.
 *
 * @author: Igor Kupczynski
 */
object ExampleFactory {

  /**
   * Converts solution to example using skipping decision attribute
   */
  def apply(solution: EvaluatedSolution): Example = {
    new Example(getFields(solution).toArray[Field])
  }

  /**
   * Converts solution to example including decision variable
   */
  def apply(solution: MarkedSolution, enumDomain: EnumDomain,
            order: List[Tuple2[Goal, Int]]): Example = {
    val fields: List[Field] = getFields(solution, order) ::: List(
      new EnumField(if (solution.good) 1 else 0, enumDomain))
    new Example(fields.toArray[Field])
  }

  /**
   * Perform extraction of non-decision attributes from solution
   */
  private def getFields(solution: EvaluatedSolution,
                        order: List[Tuple2[Goal, Int]]): List[Field] = {
    var fields: List[Field] = new StringField("Solution") :: Nil
    order foreach { case (g, p) =>
      fields = new FloatField(solution.getPercentile(g, p)) :: fields
    }
    fields.reverse
  }

  /**
   * Perform extraction of non-decision attributes from solution.
   * Use default ordering.
   */
  private def getFields(solution: EvaluatedSolution): List[Field] = {
    val sim = solution.sim
    var fields: List[Field] = new StringField("Solution") :: Nil
    sim.problem.goals foreach (g => {
      sim.config.PERCENTILES foreach (p => {
        fields = new FloatField(solution.getPercentile(g, p)) :: fields
      })
    })
    fields.reverse
  }

}
