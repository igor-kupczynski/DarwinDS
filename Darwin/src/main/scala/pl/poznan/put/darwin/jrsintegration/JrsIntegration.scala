package pl.poznan.put.darwin.jrsintegration

import pl.poznan.put.cs.idss.jrs.core.Transfer
import pl.poznan.put.cs.idss.jrs.core.mem.{MemoryOutput, MemoryContainer}
import pl.poznan.put.cs.idss.jrs.wrappers.{VCdomLEMWrapperOpt, RulesGeneratorWrapper}
import pl.poznan.put.cs.idss.jrs.rules.{RulesContainer}
import java.io.File
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.solution.MarkedSolution

object JrsIntegration {
  var counter = 0

  def apply(result: List[MarkedSolution]): AbstractRulesContainer = {
    val sim = result(0).sim
    if (sim.config.ALL_RULES) {
      getAllRulesContainer(result)
    } else {
      var container: RulesContainer = getSingleContainer(result, 0, false)
      counter = counter + 1
      if (sim.config.MULTI_RULES) {
        var containers = List(container)
        for (idx <- 1 to sim.config.MULTI_RULES_COUNT) {
          containers = containers :+ getSingleContainer(result, idx, true)
        }
        DarwinRulesContainer(containers, result)
      } else {
        DarwinRulesContainer(container, result)
      }
    }
  }


  private def getAllRulesContainer(result: List[MarkedSolution]): AbstractRulesContainer = {
    null
  }
  
  
  private def getSingleContainer(result: List[MarkedSolution], id: Int, randomize: Boolean):
  RulesContainer = {
    val sim = result(0).sim
    val rulesInput = new RulesInput(result, randomize)
    val mc: MemoryContainer = new MemoryContainer()
    Transfer.transfer(rulesInput, new MemoryOutput(mc))
    val wrapper: RulesGeneratorWrapper =
      new VCdomLEMWrapperOpt(mc, sim.config.DOMLEM_CONFIDECE_LEVEL,
                             sim.config.CONDITION_SELECTION_METHOD,
                             sim.config.NEGATIVE_EXAMPLES_TREATMENT)
    wrapper.setInducePossibleRules(false)
    val container: RulesContainer = wrapper.generateRules(mc)
    container.writeRules("rules/rule_%03d_%03d.txt".format(id, counter), true, true)
    container
  }
}
