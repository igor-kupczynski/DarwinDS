package pl.poznan.put.darwin.utils

import pl.poznan.put.darwin.model.problem.Goal
import pl.poznan.put.darwin.model.Solution

object SysOutPresenter {

  def showOutput(result: List[Solution]) {
    result foreach ((sol: Solution) => {
      sol.goals foreach ((g: Goal) => {
          println("[%s] 1 => %f, 25 => %f, 50 => %f (good: %s) ::: value = %f" format (g.name,
                    sol.getPercentile(g, 1.0), sol.getPercentile(g, 25.0),
                    sol.getPercentile(g, 50.0), sol.isGood, sol.utilityFunctionValue))
        })
      }
    )
  }
}