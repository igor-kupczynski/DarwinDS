package pl.poznan.put.allrules

import model._
import java.io.{FileWriter, File}
import pl.poznan.put.darwin.utils.TimeUtils
import collection.mutable.ListBuffer

class AllRules[+T](table: Table[T]) {
  
  def rulesFromConcepts[U >: T](objLB: Set[Map[Column[Any], Any]], c: Concept[U])(implicit ord2: Ordering[U]): scala.List[Rule[Any]] = {
    val rules = new ListBuffer[Rule[Any]]
    implicit val ord = ord2.asInstanceOf[Ordering[T]]
    for (obj <- objLB) {
      if (c.upwards) {
        if (objLB.forall({x => (obj == x || !table.stronglyDominates(obj, x))})) {
          rules += table.toRule(obj, true, c.values)
        }
      } else {
        if (objLB.forall({x => (obj == x || !table.stronglyDominates(x, obj))})) {
          rules += table.toRule(obj, false, c.values)
        }
      }
    }
    rules.toList
  }

  def generate[U >: T](debug: Boolean)(implicit ord2: Ordering[U]): List[Rule[Any]] = {
    val rules = new ListBuffer[Rule[Any]]
    implicit val ord = ord2.asInstanceOf[Ordering[T]]
    for (attrNames <- table.attributePowerset) {
      for ((c, objLB) <- table.allConceptsLB(attrNames)) {
        rules ++= rulesFromConcepts(objLB, c)
      }
    }
    rules.toList
  }

//  def minimize(List[Rule[Any]]): List[Rule[Any]] = {
//    val minimal = new ListBuffer[Rule[Any]]
//  }
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

