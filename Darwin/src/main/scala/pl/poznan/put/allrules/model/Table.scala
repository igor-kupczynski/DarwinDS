package pl.poznan.put.allrules.model

/**
 * The expetion to be thrown when creating a table
 */
case class TableException(msg:String) extends Exception

  
/**
 * The information table representation
 */
class Table[+T](cols: Set[Column[Any]]) {

   validateColumns(cols)
  
   private[model] val columns = cols
   private[model] val attributes = cols.filter(c => !c.decision)
   private[model] val decision: Column[T] = (cols.filter(c => c.decision).head).
          asInstanceOf[Column[T]]
  

  /**
   * Adds an object (as a row) to the table
   */
  def addObject(name: String, obj: Map[Column[Any], Any]) = {
    for (col <- columns) {
      col.addObject(name, obj(col))
    }
  } 
  
  /**
   * Returns all subsets of the table's attributes
   */
  def attributePowerset(): Set[Set[String]] = {
    def powerset[A](s: Set[A]) = s.foldLeft(Set(Set.empty[A])) {
      case (ss, el) => ss ++ ss.map(_ + el)
    }
    powerset(attributes map {_.name}).filter(s => s != s.empty)
  }

  def decisionPowerset(): Set[Set[String]] = {
    attributePowerset map { _ + decision.name }
  }
  
  /**
   * Returns all concept from the table
   */
  def allConcepts[U >: T](implicit ord: Ordering[U]): List[Concept[U]] = {
    val ord2 = ord.asInstanceOf[Ordering[U]]
    val decisionScale =  if (decision.gain)
      decision.valueScale(ord2)
    else
      decision.valueScale(ord2).reverse
    var result: List[Concept[U]] = List()
      for(i <- (decisionScale.length -1).to(1, -1)) {
        val x = decisionScale(i)
        result = result :+ new Concept[U](true, decisionScale filter {ord2.gteq(_, x)})
      }
      for(i <- 0 to (decisionScale.length -2)) {
        val x = decisionScale(i)
        result = result :+ new Concept[U](false, decisionScale filter {ord2.lteq(_, x)})
      } 
    result
  }

  /**
   * Checks if columns are valid:
   *  - there is exactly one decision column
   */
  private def validateColumns(cols: Set[Column[Any]]) = {
    var count = 0
    for (c <- cols)
      count = count + (if (c.decision) 1 else 0)
    if (count != 1)
       throw TableException("The table should contain one decision attribute")
  }

  private def getColumnForName(name: String): Column[Any] = {
    for (c <- columns) if (c.name == name) return c
    null
  }

  private def getColumns(names: Set[String]): Set[Column[Any]] = {
    var result: Set[Column[Any]] = Set()
    for (n <- names) {
      result = result + getColumnForName(n)
    }
    result
  }

  def equalsWRT(attrNames: Set[String],
                a: Map[Column[Any], Any],
                b: Map[Column[Any], Any]): Boolean = {
    for (c <- getColumns(attrNames)) {
      if (a(c) != b(c)) return false
    }
    true
  }

  private def isBetterOnColumn[A](c: Column[A], bVal: A, aVal: A)(implicit ord: Ordering[A]) =
    (c.gain && ord.gt(bVal, aVal)) || (!c.gain && ord.lt(bVal, aVal))

  private def isStronglyBetterOnColumn[A](c: Column[A], bVal: A, aVal: A)(implicit ord: Ordering[A]) =
    (c.gain && ord.gteq(bVal, aVal)) || (!c.gain && ord.lteq(bVal, aVal))

  def weeklyDominates(a: Map[Column[Any], Any], b: Map[Column[Any], Any]): Boolean = {
    if (a.size != b.size) throw TableException(
      "Objects being compared should have the same attributes")
    for (c <- a.keys) {
      if (!c.decision && isBetterOnColumn(c, b(c), a(c))(c.ord)) {
        return false
      }
    }
    true
  }

  def stronglyDominates(a: Map[Column[Any], Any], b: Map[Column[Any], Any]): Boolean = {
    if (a.size != b.size) throw TableException(
      "Objects being compared should have the same attributes")
    for (c <- a.keys) {
      if (!c.decision && isStronglyBetterOnColumn(c, b(c), a(c))(c.ord)) {
            return false
          }
    }
    true
  }
  
  private def isInOpositeConcepts[U >: T](obj: Map[Column[Any], Any],
        objConcept: Concept[U],
        concepts: Map[Concept[U], Set[Map[Column[Any], Any]]])
        (implicit ord: Ordering[U]):
      Boolean = {
    val ord2 = ord.asInstanceOf[Ordering[T]]
    val opposition = concepts.filter(_._1.upwards != objConcept.upwards)
    for (c <- opposition) {
      if (objConcept.upwards) {
        c._2 foreach { x => if (weeklyDominates(x, obj)) {
          return true
        }}
      } else {
        c._2 foreach { x => if (weeklyDominates(obj, x)) {
          return true
        }}
      }
    }
    false
  }
  
  def objectByName(name: String): Map[Column[Any], Any] = {
    Map(columns.map(
      {c => c -> c.rows.apply(name)}
    ).toSeq: _*)
  }

  def filterAttributes(attrNames: Set[String], obj: Map[Column[Any], Any]):
      Map[Column[Any], Any] ={
    obj.filterKeys({
      k => {attrNames.contains(k.name) || k.name == decision.name}
    })
  }

  def allConceptsUB[U >: T](attrNames: Set[String])(implicit ord: Ordering[U]):
      Map[Concept[U], Set[Map[Column[Any], Any]]] = {
    val concepts: List[Concept[U]] = allConcepts(ord)
    Map(concepts.map(
      {con => con ->
              decision.objectsForConcept(con).map({ name =>
                filterAttributes(attrNames, objectByName(name))
      })
    }): _*)
  }

  def allConceptsLB[U >: T](attrNames: Set[String])(implicit ord: Ordering[U]):
      Map[Concept[U], Set[Map[Column[Any], Any]]] = {
    val ord2 = ord.asInstanceOf[Ordering[T]]
    val ubs = allConceptsUB(attrNames)(ord2)
    var result: Map[Concept[U], Set[Map[Column[Any], Any]]] = Map()
    for (c <- ubs.keys) {
      result = result + (c -> ubs(c).filter(!isInOpositeConcepts(_, c, ubs)(ord2)))
    }
    result
  }

  def toRule[U >: T](obj: Map[Column[Any], Any], atLeast: Boolean, values: List[U])
    (implicit cmp: Ordering[U]):
      Rule[U] =
    Rule[U](
      obj.keys
        .filter({c => !c.decision})
        .map({c => c.toRuleCondition(obj, atLeast)})
        .toList,
      atLeast,
      if (atLeast)
        values.asInstanceOf[List[U]].min
      else
        values.asInstanceOf[List[U]].max
    )
}

object Main {

  def main(args: Array[String]) {
    println("Hello world!")
    val c1: Column[Any] = ColumnFactory.get[Int]("c1", false, true)
    val c2: Column[Any] = ColumnFactory.get[Int]("d", true, true)
    val t = new Table[Int](Set(c1, c2))
  }

}
