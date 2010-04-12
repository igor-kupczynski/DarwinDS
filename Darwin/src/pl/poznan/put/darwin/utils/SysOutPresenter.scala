package pl.poznan.put.darwin.utils

import pl.poznan.put.darwin.experiment.SolutionResult
import collection.mutable.HashMap
import pl.poznan.put.darwin.model.Config.Solution
import pl.poznan.put.darwin.model.problem.Goal

object SysOutPresenter {

  def showOutput(result: List[Tuple2[Solution, SolutionResult]]) {
    result foreach {
      case (sol, res: SolutionResult) => {
      res.goals foreach ((g: Goal) => {
          println("[%s] 1 => %f, 25 => %f, 50 => %f (good: %s) ::: value = %f" format (g.name,
                    res.getPercentile(g, 1.0), res.getPercentile(g, 25.0),
                    res.getPercentile(g, 50.0), res.isGood, res.autoValue))
        })
      }
    }
  }

  def showOutput(result: HashMap[Solution, SolutionResult]) {
    this.showOutput(result.toList)
  }
}