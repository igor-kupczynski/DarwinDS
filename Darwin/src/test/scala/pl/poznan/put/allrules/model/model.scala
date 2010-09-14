package pl.poznan.put.allrules.model

import org.specs.Specification

class ColumnTest extends Specification {

  var power: Column[Int] = null
  var price: Column[Int] = null
  var dec: Column[Int] = null

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

  "Column" should {
    init()
    "be initaly empty" in {
      power.rows.size must be_==(0)
      price.rows.size must be_==(0)
      dec.rows.size must be_==(0)
    }
    "accept learning examples" in {
      power.addObject("a", a("power").asInstanceOf[Int])
      power.rows.size must be_==(1)
      power.rows("a") must be_==(a("power"))

      power.addObject("b", b("power").asInstanceOf[Int])
      power.rows.size must be_==(2)
      power.rows("b") must be_==(b("power"))
    }
    "correctly calculate value scale" in {
      addAll()
      power.valueScale must be_==(List(2, 3))
      price.valueScale must be_==(List(1, 2))
      dec.valueScale must be_==(List(0, 1))
    }
  }
  
  private def init() {
    power = Column[Int]("Power", false, true)
    price = Column[Int]("Price", false, false)
    dec = Column[Int]("Dec", true, true)
  }

  private def addAll() = {
    power.addObject("a", a("power").asInstanceOf[Int])
    power.addObject("b", b("power").asInstanceOf[Int])

    price.addObject("a", a("price").asInstanceOf[Int])
    price.addObject("b", b("price").asInstanceOf[Int])

    dec.addObject("a", a("dec").asInstanceOf[Int])
    dec.addObject("b", b("dec").asInstanceOf[Int])
  }
  
}
