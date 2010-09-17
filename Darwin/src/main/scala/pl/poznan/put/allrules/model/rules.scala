package pl.poznan.put.allrules.model

case class RuleCondition[+T](field: String, gt: Boolean, value: T) {
  
  def covers(obj: Map[String, Any]): Boolean = {
    val objAnyVal = obj(field)

    // Ugly, should be changed to something better
  
    if (objAnyVal.isInstanceOf[Int]) {
      val objVal: Int = obj(field).asInstanceOf[Int]
      if (gt)
        (objVal >= value.asInstanceOf[Int])
      else
        (objVal <= value.asInstanceOf[Int])
    } else if (objAnyVal.isInstanceOf[Float]) {
      val objVal: Float = obj(field).asInstanceOf[Float]
      if (gt)
        (objVal >= value.asInstanceOf[Float])
      else
        (objVal <= value.asInstanceOf[Float])
    } else if (objAnyVal.isInstanceOf[Double]) {
      val objVal: Double = obj(field).asInstanceOf[Double]
      if (gt)
        (objVal >= value.asInstanceOf[Double])
      else
        (objVal <= value.asInstanceOf[Double])
    } else {
      throw TableException("Can not cast to Int of Float or Double")
    }
  }

  override def toString =
    "(%s %s %s)" format (field, if (gt) ">=" else "<=", value)
}
  
case class Rule[+T](conditions: List[RuleCondition[Any]], atLeast: Boolean, value: T) {

  /**
   * Checks if a rule is smaller than the other one. Smaller means the same
   * decision but smaller number of conditions.
   */
  def isSmallerThan[U >: T](other: Rule[U]): Boolean = {
    if (this.atLeast != other.atLeast || this.value != other.value)
      return false
    for (c <- this.conditions) {
      if (!other.conditions.contains(c)) {
        // Different condition, can not compare
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
