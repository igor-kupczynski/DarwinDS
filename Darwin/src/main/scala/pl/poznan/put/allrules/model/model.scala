package pl.poznan.put.allrules.model

import collection.SortedSet
  
/** 
 * A class representing a single column in the information table
 */
case class Column[+T](name: String, decision: Boolean, gain: Boolean) {

  private[model] var _ord: Ordering[Any] = _
  def ord[U >: T]: Ordering[U] = {
    _ord.asInstanceOf[Ordering[U]]
  }

  var _rows: Map[String, Any] = Map()

  def rows[U >: T] = _rows
  
  def addObject[B >: T](name: String, value: B) = {
    _rows = _rows + (name -> value)
  }

  private def values(): List[T] = rows.iterator.map(_._2).toList.asInstanceOf[List[T]] 
  
  def valueScale[U >: T](implicit ord: Ordering[U]): List[T] = {
    val ord2 = ord.asInstanceOf[Ordering[T]]
    SortedSet(values: _*)(ord2).toList
  }

  def objectsForValue[U >: T](value: U): Set[String] = Set(
    rows.iterator.filter(_._2 == value).toList.map(_._1): _*)

  def objectsForConcept[U >: T](c: Concept[U]): Set[String] = {
    var result: Set[String] = Set()
    for (v <- c.values) {
      result = result ++ objectsForValue(v)
    }
    result
  }
  
  def toRuleCondition(obj: Map[Column[Any], Any], dec: Boolean):
      RuleCondition[T] = {
    RuleCondition[T](name, if (dec) gain else !gain, obj(this).asInstanceOf[T])
  }
}

case class Concept[+T](upwards: Boolean, values: List[T])  

object ColumnFactory {
  def get[T](name: String, decision: Boolean, gain: Boolean)(implicit ord: Ordering[T]): Column[T] = {
    val c = Column[T](name, decision, gain)
    c._ord = ord.asInstanceOf[Ordering[Any]]
    c
  }
}


