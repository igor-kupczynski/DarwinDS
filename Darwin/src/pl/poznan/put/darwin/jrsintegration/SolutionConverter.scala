package pl.poznan.put.darwin.jrsintegration

import pl.poznan.put.cs.idss.jrs.types.{StringField, FloatField, Field}
import pl.poznan.put.darwin.model.problem.Goal
import pl.poznan.put.darwin.model.{Solution, Config}

object SolutionConverter {
  def getFields(solution: Solution): List[Field] = {
    var fields: List[Field] = new StringField("Solution") :: Nil
    solution.goals foreach ((g: Goal) => {
      Config.PERCENTILES foreach (p => {
        fields = new FloatField(solution.getPercentile(g, p)) :: fields
      })
    })
    fields.reverse
  }
}