package pl.poznan.put.darwin.simulation

import org.scalacheck.Prop._
import org.specs.{ScalaCheck, Specification}

class DMMockTest extends Specification with ScalaCheck {

  val dm = new DMMock(null)
  
  "DMMock" should {
    "correctly calculate number of solutions to select" in {
      forAll { (a: Int) => dm.getGoodCount(a, 0) == a
      } must pass
      forAll { (a: Int, b: Int) => (a >= 0 && a < 1000000 && b > 0 && b < 1000000) ==> {
        val res = dm.getGoodCount(a, b)
        (res >= (a - b) && res <= (a + b))
      }} must pass

      // Lets check if all values are present :-)
      var result = Map(1 -> 0, 2 -> 0, 3 -> 0, 4 -> 0, 5 -> 0)
      for (idx <- 1 to 100) {
        val r = dm.getGoodCount(3, 2)
        result = result + ((r, result(r) + 1))
      }
      for (idx <- 1 to 5) {
        result(idx) must be_>(0)
      }
    }
  }
  
}
