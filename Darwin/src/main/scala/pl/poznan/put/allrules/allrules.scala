package pl.poznan.put.allrules

import model._
import java.io.{FileWriter, File}

class AllRules[+T](table: Table[T]) {

  def addToMinimal(item: Rule[Any], ll: List[Rule[Any]]): List[Rule[Any]] = {
    ll match {
      case Nil => List(item)
      case head :: tail => {
        if (head.isSmallerThan(item)) head :: tail
        else if (item.isSmallerThan(head)) item :: tail
        else head :: addToMinimal(item, tail)
      }
    }
  }

  def rulesFromConcepts[U >: T](objLB: Set[Map[Column[Any], Any]], c: Concept[U], minimal: Boolean)
                               (implicit ord2: Ordering[U]): List[Rule[Any]] = {
    var rules: List[Rule[Any]] = List()
    implicit val ord = ord2.asInstanceOf[Ordering[T]]
    for (obj <- objLB) {
      if (c.upwards) {
        if (objLB.forall({x => (obj == x || !table.stronglyDominates(obj, x))})) {
          val r = table.toRule(obj, true, c.values)
          if (minimal)
            rules = addToMinimal(r, rules)
          else
            rules = r :: rules
        }
      } else {
        if (objLB.forall({x => (obj == x || !table.stronglyDominates(x, obj))})) {
          val r = table.toRule(obj, false, c.values)
          if (minimal)
            rules = addToMinimal(r, rules)
          else
            rules = r :: rules
        }
      }
    }
    rules
  }

  def generate[U >: T](minimal: Boolean)(implicit ord2: Ordering[U]): List[Rule[Any]] = {
    var rules: List[Rule[Any]] = Nil
    implicit val ord = ord2.asInstanceOf[Ordering[T]]
    for (attrNames <- table.attributePowerset) {
      for ((c, objLB) <- table.allConceptsLB(attrNames)) {
        val rc = rulesFromConcepts(objLB, c, minimal)
        for (r <- rc) {
          if (minimal)
            rules = addToMinimal(r, rules)
          else
            rules = r :: rules
        }
      }
    }
    rules
  }
}


object AllRules {

  def saveToFile(name: String, rules: List[Rule[Any]]) {
    val sb = new StringBuilder()
    sb.append("--- Rules ---\n\n")
    sb.append("[RULES]\n#Certain at least rules\n")
    var idx = 0
    for (r <- rules) {
      if (r.atLeast) {
        idx += 1
        sb.append("%s: %s\n" format (idx, r))
      }
    }
    idx = 0
    sb.append("#Certain at most rules\n")
    for (r <- rules) {
      if (!r.atLeast) {
        idx += 1
        sb.append("%s: %s\n" format (idx, r))
      }
    }
    val f = new File(name)
    val fw = new FileWriter(f)
    try{
      fw.write(sb.toString)
    } finally {
      fw.close
    }
  }
}

