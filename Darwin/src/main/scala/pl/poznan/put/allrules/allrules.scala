package pl.poznan.put.allrules

import model._
import java.io.{FileWriter, File}
import pl.poznan.put.darwin.utils.TimeUtils
import collection.mutable.ListBuffer

class AllRules[+T](table: Table[T]) {

  def isMinimal(item: Rule[Any], reference: List[Rule[Any]]): Boolean = {
      for (el <- reference) {
        if (el.isSmallerThan(item)) {
          return false
        }
      }
      true
  }

  def rulesFromConcepts[U >: T](objLB: Set[Map[Column[Any], Any]], c: Concept[U], minimal: Boolean)
                               (implicit ord2: Ordering[U]): List[Rule[Any]] = {
    var rules: List[Rule[Any]] = List()
    implicit val ord = ord2.asInstanceOf[Ordering[T]]
    for (obj <- objLB) {
      if (c.upwards) {
        if (objLB.forall({x => (obj == x || !table.stronglyDominates(obj, x))})) {
          val r = table.toRule(obj, true, c.values)
          if (!minimal || isMinimal(r, rules))
            rules = r :: rules
        }
      } else {
        if (objLB.forall({x => (obj == x || !table.stronglyDominates(x, obj))})) {
          val r = table.toRule(obj, false, c.values)
          if (!minimal || isMinimal(r, rules))
            rules = r :: rules                
        }
      }
    }
    rules
  }

  def generate[U >: T](minimal: Boolean)(implicit ord2: Ordering[U]): List[Rule[Any]] = {
    val rules = new ListBuffer[Rule[Any]]
    implicit val ord = ord2.asInstanceOf[Ordering[T]]
    for (attrNames <- table.attributePowerset) {
      for ((c, objLB) <- table.allConceptsLB(attrNames)) {
        rules ++= rulesFromConcepts(objLB, c, minimal)
      }
    }
    if (minimal) {
      TimeUtils.time("Minimize", minimize(rules.toList))
    } else {
      rules.toList
    }
  }

  def minimize(rules: List[Rule[Any]]): List[Rule[Any]] = {

    def extractMinimal(toExtract: List[Rule[Any]], minimal: List[Rule[Any]]): List[Rule[Any]] = {
      if (toExtract.length == 0) minimal
      else if (isMinimal(toExtract.head, minimal ::: toExtract.tail))
        extractMinimal(toExtract.tail, toExtract.head :: minimal)
      else extractMinimal(toExtract.tail, minimal)
    }

    extractMinimal(rules, List())
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

