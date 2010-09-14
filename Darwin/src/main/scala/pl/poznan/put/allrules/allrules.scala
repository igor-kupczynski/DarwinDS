package pl.poznan.put.allrules

import model._

class AllRules[+T](table: Table[T]) {
  
  def generate[U >: T](implicit ord2: Ordering[U]): List[Rule[Any]] = {
    implicit val ord = ord2.asInstanceOf[Ordering[T]]
    var rules: List[Rule[Any]] = List()
    for (attrNames <- table.attributePowerset) {
      for ((c, objLB) <- table.allConceptsLB(attrNames)) {
        for (obj <- objLB) {
          if (c.upwards) {
            if (objLB.forall({x => (obj == x || !table.stronglyDominates(obj, x))})) {
              rules = rules :+ table.toRule(obj, true, c.values)
            }
          } else {
            if (objLB.forall({x => (obj == x || !table.stronglyDominates(x, obj))})) {
              rules = rules :+ table.toRule(obj, false, c.values)
            }
          }
        }
      }
    }
    rules
  }
}

