package pl.poznan.put.allrules.model

import org.specs.Specification

class TableTest extends Specification {
  val power = ColumnFactory.get[Int]("Power", false, true)
  val cost = ColumnFactory.get[Int]("Cost", false, false)
  val dec = ColumnFactory.get[Int]("Decision", true, true)
  val cols: Set[Column[Any]] = Set(power, cost, dec)


  val o1: Map[Column[Any], Any] = Map(power -> 10, cost -> 10, dec -> 1)
  val o2: Map[Column[Any], Any] = Map(power ->  2, cost ->  2, dec -> 0)
  val o3: Map[Column[Any], Any] = Map(power ->  5, cost ->  2, dec -> 1)
  val o4: Map[Column[Any], Any] = Map(power ->  3, cost ->  2, dec -> 0)

  val c1 = Concept[Int](true, List(1))
  val c2 = Concept[Int](false, List(0))
  
  val o5: Map[Column[Any], Any] = Map(power ->  10, cost ->  3, dec -> 2)
  
  var t: Table[Int] = _
  
  "Table" should {
    initTable()
    "contain defined columns" in {
      t.columns.size must be_==(3)
      t.columns must be_==(cols)
      t.attributes must be_==(Set(power, cost))
      t.decision must be_==(dec)
    }
    "store added objects" in {
      for (c <- t.columns) {
        c.rows.size must be_==(4)
        c.rows must haveKey("1")
        c.rows must haveKey("2")
        c.rows must haveKey("3")
        c.rows must haveKey("4")
      }
    }
    "return powerset of attributes" in {
      val powerset = Set(
        Set("Power", "Decision"), Set("Cost", "Decision"), Set("Power", "Cost", "Decision"))
      t.decisionPowerset must haveTheSameElementsAs(powerset)
    }
    "return all possible concepts" in {
      val concepts = Set(c1, c2)
      t.allConcepts must haveTheSameElementsAs(concepts)

      t.addObject("5", o5)
      val concepts2 = Set(Concept[Int](true, List(2)),  Concept[Int](true, List(1, 2)),
                          Concept[Int](false, List(0)), Concept[Int](false, List(0, 1))
                      )
      t.allConcepts must haveTheSameElementsAs(concepts2)
    }
    "return concepts' upper bounds" in {
      t.decision.objectsForConcept(c1) must haveTheSameElementsAs(List("1", "3"))
      t.decision.objectsForConcept(c2) must haveTheSameElementsAs(List("2", "4"))

      val ref1 = Map(
        c1 -> Set(Map(power -> 5, dec -> 1), Map(power -> 10, dec -> 1)),
        c2 -> Set(Map(power -> 2, dec -> 0), Map(power ->  3, dec -> 0))
      )
      t.allConceptsUB(Set("Power")) must haveTheSameElementsAs(ref1)

      val ref2 = Map(
        c1 -> Set(Map(cost -> 10, dec -> 1), Map(cost -> 2, dec -> 1)),
        c2 -> Set(Map(cost -> 2, dec -> 0))
      )
      t.allConceptsUB(Set("Cost")) must haveTheSameElementsAs(ref2)

      val ref3 = Map(
        c1 -> Set(Map(power -> 10, cost -> 10, dec -> 1),
                  Map(power ->  5, cost ->  2, dec -> 1)),
        c2 -> Set(Map(power -> 2, cost -> 2, dec -> 0),
                  Map(power -> 3, cost -> 2, dec -> 0))
      )
      t.allConceptsUB(Set("Power", "Cost")) must haveTheSameElementsAs(ref3)
    }
    "return concepts' lower bounds" in {
      val ref1 = Map(
        c1 -> Set(Map(power -> 5, dec -> 1), Map(power -> 10, dec -> 1)),
        c2 -> Set(Map(power -> 2, dec -> 0), Map(power ->  3, dec -> 0))
      )
      t.allConceptsLB(Set("Power")) must haveTheSameElementsAs(ref1)

      val ref2 = Map(
        c1 -> Set(),
        c2 -> Set()
      )
      t.allConceptsLB(Set("Cost")) must haveTheSameElementsAs(ref2)

      val ref3 = Map(
        c1 -> Set(Map(power -> 10, cost -> 10, dec -> 1),
                  Map(power ->  5, cost ->  2, dec -> 1)),
        c2 -> Set(Map(power -> 2, cost -> 2, dec -> 0),
                  Map(power -> 3, cost -> 2, dec -> 0))
      )
      t.allConceptsLB(Set("Power", "Cost")) must haveTheSameElementsAs(ref3)
    }
  }

  def initTable() = {
    t = new Table[Int](cols)
    t.addObject("1", o1)
    t.addObject("2", o2)
    t.addObject("3", o3)
    t.addObject("4", o4)
  }
}
