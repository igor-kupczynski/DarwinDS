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

  def apply(result: List[MarkedSolution]): DarwinRulesContainer = {
    val sim = result(0).sim
    val rulesInput = new RulesInput(result)
    val mc: MemoryContainer = new MemoryContainer()
    Transfer.transfer(rulesInput, new MemoryOutput(mc))
    val wrapper: RulesGeneratorWrapper =
      new VCdomLEMWrapperOpt(mc, sim.config.DOMLEM_CONFIDECE_LEVEL,
      sim.config.CONDITION_SELECTION_METHOD,
      sim.config.NEGATIVE_EXAMPLES_TREATMENT)
    wrapper.setInducePossibleRules(false);
    val container: RulesContainer = wrapper.generateRules(mc)
    val cwd = new File(".").getAbsolutePath
    container.writeRules("rules/rule_%03d.txt".format(counter), true, true)
    counter = counter + 1
    SingleRulesContainer(container, result)
  }
}
