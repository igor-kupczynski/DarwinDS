package pl.poznan.put.allrules.model

import org.specs.Specification

class RulesTest extends Specification {
  
  var a: Map[String, Any] = Map(
    "power" -> 3,
    "price" -> 1,
    "dec" -> 1
  )
  
  var b: Map[String, Any] = Map(
    "power" -> 2,
    "price" -> 2,
    "dec" -> 0
  )

  val rc1 = RuleCondition[Int]("power", true, 2)
  val rc2 = RuleCondition[Int]("power", true, 4)
  val rc3 = RuleCondition[Int]("price", false, 1)
  val rc4 = RuleCondition[Int]("price", true, 2)
  
  "RuleCondition" should {
    "match objects only covered by the condition" in {
      rc1.covers(a) must be_==(true)
      rc1.covers(b) must be_==(true)

      rc2.covers(a) must be_==(false)
      rc2.covers(b) must be_==(false)

      rc3.covers(a) must be_==(true)
      rc3.covers(b) must be_==(false)

      rc4.covers(a) must be_==(false)
      rc4.covers(b) must be_==(true)
    }
  }

  val r1 = Rule[Int](List(rc1), true, 1)
  val r2 = Rule[Int](List(rc1, rc3), true, 1)
  val r3 = Rule[Int](List(rc1, rc3), false, 1)
  val r4 = Rule[Int](List(rc1, rc4), true, 3)

  val o1: Map[String, Any] = Map("power" -> 3,
                                 "price" -> 3)
  val o2: Map[String, Any] = Map("power" -> 2,
                                 "price" -> 1)
  
  "Rule" should {
    "correctlly compare its size" in {
      r1.isSmallerThan(r2) must be_==(true)
      r1.isSmallerThan(r3) must be_==(false)
      r1.isSmallerThan(r4) must be_==(false)
    }
    "it covers a given example" in {
      r1.covers(o1) must be_==(true)
      r2.covers(o1) must be_==(false)
      r1.covers(o2) must be_==(true)
      r2.covers(o2) must be_==(true)
    }
    
  }
  
}
