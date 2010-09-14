package pl.poznan.put.allrules

import model._
import org.specs.Specification

class AllRulesTest extends Specification {

  val power = Column[Int]("Power", false, true)
  val cost = Column[Int]("Cost", false, false)
  val dec = Column[Int]("Decision", true, true)
  val cols: Set[Column[Any]] = Set(power, cost, dec)

  var t: Table[Int] = _
  
  val o1: Map[Column[Any], Any] = Map(power -> 10, cost -> 10, dec -> 1)
  val o2: Map[Column[Any], Any] = Map(power ->  2, cost ->  2, dec -> 0)
  val o3: Map[Column[Any], Any] = Map(power ->  5, cost ->  2, dec -> 1)
  val o4: Map[Column[Any], Any] = Map(power ->  3, cost ->  2, dec -> 0)

  def initTable() = {
    t = new Table[Int](cols)
    t.addObject("1", o1)
    t.addObject("2", o2)
    t.addObject("3", o3)
    t.addObject("4", o4)
  }

  def rules = List(
    Rule[Int](List(RuleCondition[Int]("Power",true,5)),true,1),
    Rule[Int](List(RuleCondition[Int]("Power",false,3)),false,0),
    Rule[Int](List(RuleCondition[Int]("Power",true,10),
                   RuleCondition[Int]("Cost",false,10)),true,1),
    Rule[Int](List(RuleCondition[Int]("Power",true,5),
                   RuleCondition[Int]("Cost",false,2)),true,1),
    Rule[Int](List(RuleCondition[Int]("Power",false,2),
                   RuleCondition[Int]("Cost",true,2)),false,0),
    Rule[Int](List(RuleCondition[Int]("Power",false,3),
                   RuleCondition[Int]("Cost",true,2)),false,0)
  )

  "AllRules" should {
    initTable()
    "generate rules" in {
      val a = new AllRules(t)
      a.generate must haveTheSameElementsAs(rules)
      
    }
  }
}
