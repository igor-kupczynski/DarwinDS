package pl.poznan.put.darwin.jrsintegration

import pl.poznan.put.cs.idss.jrs.core.Transfer
import pl.poznan.put.cs.idss.jrs.core.mem.{MemoryOutput, MemoryContainer}
import pl.poznan.put.cs.idss.jrs.wrappers.{VCdomLEMWrapperOpt, RulesGeneratorWrapper}
import pl.poznan.put.cs.idss.jrs.rules.{RulesContainer}
import pl.poznan.put.darwin.model.solution.MarkedSolution
import pl.poznan.put.allrules.AllRules
import pl.poznan.put.allrules.model.{ColumnFactory, Column, Table}
import pl.poznan.put.darwin.utils.TimeUtils

object JrsIntegration {
  var counter = 0

  def apply(result: List[MarkedSolution]): AbstractRulesContainer = {
    counter = counter + 1
    val sim = result(0).sim
    if (sim.config.ALL_RULES) {
      getAllRulesContainer(result)
    } else {
      var container: RulesContainer = getSingleContainer(result, 0, false)
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
    val sim = result(0).sim
    val goals = result(0).goals
    var columns: Map[String, Column[Any]] = Map()
    for (g <- goals) {
      for (p <- sim.config.PERCENTILES) {
        val name = "%s_%s" format (g.name, p)
        columns = columns + (name -> ColumnFactory.get[Double](name, false, g.max))
      }
    }
    columns = columns + ("Good" -> ColumnFactory.get[Boolean]("Good", true, true))
    val t = new Table[Boolean](Set(columns.values.toList: _*))
    for (s <- result.zipWithIndex) {
      t.addObject(
        s._2.toString,
        ObjectFactory(s._1, columns)
      )
    }
    val rules = (new AllRules[Boolean](t)).generate(true)
    AllRules.saveToFile("rules/rule_%03d.txt".format(counter), rules)
    ARRulesContainer(rules, result)
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
