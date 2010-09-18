package pl.poznan.put.allrules.model

case class RuleCondition[+T](column: Column[T], gt: Boolean, value: T) {

  def isSmallerThan[U >: T](other: RuleCondition[U]) : Option[Boolean] = {
    if (this.column != other.column || this.gt != other.gt) {
      return None
    }
    val result = if (gt) column.ord.lt(value, other.value)
    else column.ord.gt(value, other.value)
    Some(result)
  }

  def covers(obj: Map[String, Any]): Boolean = {
    val objVal = obj(column.name)

    if (gt) column.ord.gteq(objVal, value)
    else column.ord.lteq(objVal, value)
  }

  override def toString =
    "(%s %s %s)" format (column.name, if (gt) ">=" else "<=", value)
}
  
case class Rule[+T](conditions: List[RuleCondition[Any]], atLeast: Boolean, value: T) {

  /**
   * Checks if a rule is smaller than the other one. Smaller means the same
   * decision but smaller number of conditions.
   */
  def isSmallerThan[U >: T](other: Rule[U]): Boolean = {
    if (this.atLeast != other.atLeast || this.value != other.value) {
      return false
    }
    if (this.conditions.length > other.conditions.length) {
      return false
    }
    for (c <- this.conditions) {
      var oneNotUndef = false
      for (oc <- other.conditions) {
        val sss = oc.isSmallerThan(c)
        if (sss != None) {
          oneNotUndef = true
          if (sss.get) {
            return false
          }
        }
      }
      if (!oneNotUndef) {
        return false
      }
    }
    true
  }

  def covers(obj: Map[String, Any]): Boolean = {
    for (c <- conditions) {
      if (!c.covers(obj)) return false
    }
    true
  }

  override def toString = {
    val fff = conditions.foldLeft("")({(a, b) => "%s & %s" format (a, b)})
    "%s => (dec %s %s)" format (fff, if (atLeast) ">=" else "<=", value)
  }
}
