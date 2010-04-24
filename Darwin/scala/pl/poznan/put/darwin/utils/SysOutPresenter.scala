package pl.poznan.put.darwin.utils

import pl.poznan.put.darwin.model.problem.Goal
import pl.poznan.put.darwin.model.solution.MarkedSolution

object SysOutPresenter {

  def showOutput(result: List[MarkedSolution]) {
    result foreach ((sol: MarkedSolution) => {
      sol.goals foreach ((g: Goal) => {
          println("[%s] 1 => %f, 25 => %f, 50 => %f (good: %s) ::: value = %f" format (g.name,
                    sol.getPercentile(g, 1.0), sol.getPercentile(g, 25.0),
                    sol.getPercentile(g, 50.0), sol.good, sol.utilityFunctionValue))
        })
      }
    )
  }
}