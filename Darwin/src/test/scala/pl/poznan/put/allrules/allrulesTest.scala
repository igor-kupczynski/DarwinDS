package pl.poznan.put.allrules

import model._
import org.specs.Specification

class AllRulesTest extends Specification {

  // Table 1

  val power = ColumnFactory.get[Int]("Power", false, true)
  val cost = ColumnFactory.get[Int]("Cost", false, false)
  val dec = ColumnFactory.get[Int]("Decision", true, true)
  val cols: Set[Column[Any]] = Set(power, cost, dec)

  var t: Table[Int] = _
  
  val o1: Map[Column[Any], Any] = Map(power -> 10, cost -> 10, dec -> 1)
  val o2: Map[Column[Any], Any] = Map(power ->  2, cost ->  2, dec -> 0)
  val o3: Map[Column[Any], Any] = Map(power ->  5, cost ->  2, dec -> 1)
  val o4: Map[Column[Any], Any] = Map(power ->  3, cost ->  2, dec -> 0)

  val rules = List(
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

  // Table 2

  val power2 = ColumnFactory.get[Double]("Power", false, true)
  val cost2 = ColumnFactory.get[Double]("Cost", false, false)
  val dec2 = ColumnFactory.get[Boolean]("Decision", true, true)
  val cols2: Set[Column[Any]] = Set(power2, cost2, dec2)

  var t2: Table[Boolean] = _

  val o12: Map[Column[Any], Any] = Map(power2 -> 10.0, cost2 -> 10.0, dec2 -> true)
  val o22: Map[Column[Any], Any] = Map(power2 ->  2.0, cost2 ->  2.0, dec2 -> false)
  val o32: Map[Column[Any], Any] = Map(power2 ->  5.0, cost2 ->  2.0, dec2 -> true)
  val o42: Map[Column[Any], Any] = Map(power2 ->  3.0, cost2 ->  2.0, dec2 -> false)

  val rules2 = List(
    Rule[Boolean](List(RuleCondition[Double]("Power",true,5.0)),true,true),
    Rule[Boolean](List(RuleCondition[Double]("Power",false,3.0)),false,false),
    Rule[Boolean](List(RuleCondition[Double]("Power",true,10.0),
                       RuleCondition[Double]("Cost",false,10.0)),true,true),
    Rule[Boolean](List(RuleCondition[Double]("Power",true,5.0),
                       RuleCondition[Double]("Cost",false,2.0)),true,true),
    Rule[Boolean](List(RuleCondition[Double]("Power",false,2.0),
                       RuleCondition[Double]("Cost",true,2.0)),false,false),
    Rule[Boolean](List(RuleCondition[Double]("Power",false,3.0),
                   RuleCondition[Double]("Cost",true,2.0)),false,false)
  )

  // Table 3

  val power3 = ColumnFactory.get[Double]("Power", false, true)
  val cost3 = ColumnFactory.get[Boolean]("Cost", false, false)
  val dec3 = ColumnFactory.get[Boolean]("Decision", true, true)
  val cols3: Set[Column[Any]] = Set(power3, cost3, dec3)

  var t3: Table[Boolean] = _

  val o13: Map[Column[Any], Any] = Map(power3 -> 10.0, cost3 ->  true, dec3 -> true)
  val o23: Map[Column[Any], Any] = Map(power3 ->  2.0, cost3 -> false, dec3 -> false)
  val o33: Map[Column[Any], Any] = Map(power3 ->  5.0, cost3 -> false, dec3 -> true)
  val o43: Map[Column[Any], Any] = Map(power3 ->  3.0, cost3 -> false, dec3 -> false)

  val rules3 = List(
    Rule[Boolean](List(RuleCondition[Double]("Power",true,5.0)),true,true),
    Rule[Boolean](List(RuleCondition[Double]("Power",false,3.0)),false,false),
    Rule[Boolean](List(RuleCondition[Double]("Power",true,10.0),
                       RuleCondition[Boolean]("Cost",false,true)),true,true),
    Rule[Boolean](List(RuleCondition[Double]("Power",true,5.0),
                       RuleCondition[Boolean]("Cost",false,false)),true,true),
    Rule[Boolean](List(RuleCondition[Double]("Power",false,2.0),
                       RuleCondition[Boolean]("Cost",true,false)),false,false),
    Rule[Boolean](List(RuleCondition[Double]("Power",false,3.0),
                   RuleCondition[Boolean]("Cost",true,false)),false,false)
  )

  def initTable() = {
    t = new Table[Int](cols)
    t.addObject("1", o1)
    t.addObject("2", o2)
    t.addObject("3", o3)
    t.addObject("4", o4)

    t2 = new Table[Boolean](cols2)
    t2.addObject("1", o12)
    t2.addObject("2", o22)
    t2.addObject("3", o32)
    t2.addObject("4", o42)

    t3 = new Table[Boolean](cols3)
    t3.addObject("1", o13)
    t3.addObject("2", o23)
    t3.addObject("3", o33)
    t3.addObject("4", o43)
  }

  "AllRules" should {
    initTable()
    "generate rules" in {
      val a = new AllRules(t)
      a.generate must haveTheSameElementsAs(rules)

      val a2 = new AllRules(t2)
      a2.generate must haveTheSameElementsAs(rules2)

      val a3 = new AllRules(t3)
      a3.generate must haveTheSameElementsAs(rules3)
    }
  }
}
