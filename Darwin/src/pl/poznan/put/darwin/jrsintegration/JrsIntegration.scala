package pl.poznan.put.darwin.jrsintegration

import collection.mutable.HashMap
import pl.poznan.put.darwin.experiment.SolutionResult
import pl.poznan.put.darwin.model.Config.Solution
import pl.poznan.put.cs.idss.jrs.core.Transfer
import pl.poznan.put.cs.idss.jrs.core.mem.{MemoryOutput, MemoryContainer}
import pl.poznan.put.cs.idss.jrs.wrappers.{VCdomLEMWrapperOpt, RulesGeneratorWrapper}
import pl.poznan.put.cs.idss.jrs.rules.{RulesContainer, VCDomLem}
import java.io.File

object JrsIntegration {
  var counter = 0

  def getScores(result: List[Tuple2[Solution, SolutionResult]]): ScoreKeeper = {
    val rulesInput = new RulesInput(result)
    val mc: MemoryContainer = new MemoryContainer()
    Transfer.transfer(rulesInput, new MemoryOutput(mc))
    val wrapper: RulesGeneratorWrapper = new VCdomLEMWrapperOpt(mc, 1.0,
      VCDomLem.MIX_CONDITIONS_FROM_DIFFERENT_OBJECTS,
      VCDomLem.COVER_NONE_OF_NEGATIVE_EXAMPLES)
    wrapper.setInducePossibleRules(false);
    val container: RulesContainer = wrapper.generateRules(mc)
    val cwd = new File(".").getAbsolutePath
    container.writeRules("rules/rule_%03d.txt".format(counter), true, true)
    counter = counter + 1
    ScoreKeeper(container, result)
  }
}