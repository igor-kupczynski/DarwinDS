package pl.poznan.put.darwin.jrsintegration

import pl.poznan.put.darwin.model.{Config, Goal}
import pl.poznan.put.darwin.experiment.SolutionResult
import pl.poznan.put.cs.idss.jrs.types.{StringField, FloatField, Field}

object SolutionConverter {
  def getFields(solutionResult: SolutionResult): List[Field] = {
    var fields: List[Field] = new StringField("Solution") :: Nil
    solutionResult.goals foreach ((g: Goal) => {
      Config.PERCENTILES foreach (p => {
        fields = new FloatField(solutionResult.getPercentile(g, p)) :: fields
      })
    })
    fields.reverse
  }
}