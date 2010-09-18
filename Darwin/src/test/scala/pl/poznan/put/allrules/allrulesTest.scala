package pl.poznan.put.allrules

import model._
import org.specs.Specification
import util.Random
import pl.poznan.put.darwin.utils.TimeUtils

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
    Rule[Int](List(RuleCondition[Int](power,true,5)),true,1),
    Rule[Int](List(RuleCondition[Int](power,false,3)),false,0),
    Rule[Int](List(RuleCondition[Int](power,true,10),
                   RuleCondition[Int](cost,false,10)),true,1),
    Rule[Int](List(RuleCondition[Int](power,true,5),
                   RuleCondition[Int](cost,false,2)),true,1),
    Rule[Int](List(RuleCondition[Int](power,false,2),
                   RuleCondition[Int](cost,true,2)),false,0),
    Rule[Int](List(RuleCondition[Int](power,false,3),
                   RuleCondition[Int](cost,true,2)),false,0)
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
    Rule[Boolean](List(RuleCondition[Double](power2,true,5.0)),true,true),
    Rule[Boolean](List(RuleCondition[Double](power2,false,3.0)),false,false),
    Rule[Boolean](List(RuleCondition[Double](power2,true,10.0),
                       RuleCondition[Double](cost2,false,10.0)),true,true),
    Rule[Boolean](List(RuleCondition[Double](power2,true,5.0),
                       RuleCondition[Double](cost2,false,2.0)),true,true),
    Rule[Boolean](List(RuleCondition[Double](power2,false,2.0),
                       RuleCondition[Double](cost2,true,2.0)),false,false),
    Rule[Boolean](List(RuleCondition[Double](power2,false,3.0),
                   RuleCondition[Double](cost2,true,2.0)),false,false)
  )

  var t2b: Table[Boolean] = _
  val o52: Map[Column[Any], Any] = Map(power2 ->  1.0, cost2 ->  1.0, dec2 -> true)

  val rules2b = List(
    Rule[Boolean](List(RuleCondition[Double](power2,true,5.0)),true,true),
    Rule[Boolean](List(RuleCondition[Double](power2,false,3.0), RuleCondition[Double](cost2,true,2.0)),false,false),
    Rule[Boolean](List(RuleCondition[Double](cost2,false,1.0)),true,true)
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
    Rule[Boolean](List(RuleCondition[Double](power3,true,5.0)),true,true),
    Rule[Boolean](List(RuleCondition[Double](power3,false,3.0)),false,false),
    Rule[Boolean](List(RuleCondition[Double](power3,true,10.0),
                       RuleCondition[Boolean](cost3,false,true)),true,true),
    Rule[Boolean](List(RuleCondition[Double](power3,true,5.0),
                       RuleCondition[Boolean](cost3,false,false)),true,true),
    Rule[Boolean](List(RuleCondition[Double](power3,false,2.0),
                       RuleCondition[Boolean](cost3,true,false)),false,false),
    Rule[Boolean](List(RuleCondition[Double](power3,false,3.0),
                   RuleCondition[Boolean](cost3,true,false)),false,false)
  )


  // Table 4
  val power41 = ColumnFactory.get[Double]("Power01", false, true)
  val power42 = ColumnFactory.get[Double]("Power02", false, true)
  val power43 = ColumnFactory.get[Double]("Power03", false, true)
  val power44 = ColumnFactory.get[Double]("Power04", false, true)
  val power45 = ColumnFactory.get[Double]("Power05", false, true)
  val power46 = ColumnFactory.get[Double]("Power06", false, true)
  val cost41 = ColumnFactory.get[Double]("Cost01", false, false)
  val cost42 = ColumnFactory.get[Double]("Cost02", false, false)
  val cost43 = ColumnFactory.get[Double]("Cost03", false, false)
  val cost44 = ColumnFactory.get[Double]("Cost04", false, false)
  val cost45 = ColumnFactory.get[Double]("Cost05", false, false)
  val cost46 = ColumnFactory.get[Double]("Cost06", false, false)
  val dec4 = ColumnFactory.get[Boolean]("Decision", true, true)

  val cols4: Set[Column[Any]] = Set(power41, cost41,
                                    power42, cost42,
                                    power43, cost43,
                                    power44, cost44,
                                    power45, cost45,
                                    power46, cost46, dec4)

    def createObject(power: Double, cost: Double, dec: Boolean): Map[Column[Any], Any] = {
      val r = new Random()
      Map(power41 -> (power + r.nextGaussian), cost41 -> (cost + r.nextGaussian),
          power42 -> (power + r.nextGaussian), cost42 -> (cost + r.nextGaussian),
          power43 -> (power + r.nextGaussian), cost43 -> (cost + r.nextGaussian),
          power44 -> (power + r.nextGaussian), cost44 -> (cost + r.nextGaussian),
          power45 -> (power + r.nextGaussian), cost45 -> (cost + r.nextGaussian),
          power46 -> (power + r.nextGaussian), cost46 -> (cost + r.nextGaussian),
          dec4 -> dec)
    }

  var t4: Table[Boolean] = _

  val o14: Map[Column[Any], Any] = createObject(100, 100, true)
  val o24: Map[Column[Any], Any] = createObject(30, 20, false)
  val o34: Map[Column[Any], Any] = createObject(80, 20, true)
  val o44: Map[Column[Any], Any] = createObject(50, 40, true)
  val o54: Map[Column[Any], Any] = createObject(20, 10, false)
  val o64: Map[Column[Any], Any] = createObject(30, 10, false)
  val o74: Map[Column[Any], Any] = createObject(60, 80, false)
  val o84: Map[Column[Any], Any] = createObject(15, 20, false)
  val o94: Map[Column[Any], Any] = createObject(10, 30, false)

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

    t2b = new Table[Boolean](cols2)
    t2b.addObject("1", o12)
    t2b.addObject("2", o22)
    t2b.addObject("3", o32)
    t2b.addObject("4", o42)
    t2b.addObject("5", o52)

    t3 = new Table[Boolean](cols3)
    t3.addObject("1", o13)
    t3.addObject("2", o23)
    t3.addObject("3", o33)
    t3.addObject("4", o43)

    t4 = new Table[Boolean](cols4)
    t4.addObject("1", o14)
    t4.addObject("2", o24)
    t4.addObject("3", o34)
    t4.addObject("4", o44)
    //t4.addObject("5", o54)
    //t4.addObject("6", o64)
    //t4.addObject("7", o74)
    //t4.addObject("8", o84)
    //t4.addObject("9", o94)
  }

  "AllRules" should {
    initTable()
    "generate rules" in {
//      val a = new AllRules(t)
//      a.generate(false) must haveTheSameElementsAs(rules)
//
//      val a2 = new AllRules(t2)
//      a2.generate(false) must haveTheSameElementsAs(rules2)

      val a2b = new AllRules(t2b)
      a2b.generate(true) must haveTheSameElementsAs(rules2b)

//      val a3 = new AllRules(t3)
//      a3.generate(false) must haveTheSameElementsAs(rules3)
//
//      val a4 = new AllRules(t4)
//      println ("---Time test---")
//      TimeUtils.time("Generate", a4.generate(false))
//      TimeUtils.time("Generate-minimize", a4.generate(true))
    }
  }
}
