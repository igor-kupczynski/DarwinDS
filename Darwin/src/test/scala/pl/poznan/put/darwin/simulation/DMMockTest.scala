package pl.poznan.put.darwin.simulation

import org.scalacheck.Prop._
import org.specs.mock.Mockito
import org.specs.{ScalaCheck, Specification}
import pl.poznan.put.darwin.model.solution.EvaluatedSolution

class DMMockTest extends Specification with Mockito with ScalaCheck {

  val dm = new DMMock(null)

  
  "DMMock" should {
    "correctly calculate number of solutions to select" in {
      forAll { (a: Int) => dm.getGoodCount(a, 0) == a
      } must pass
      forAll { (a: Int, b: Int) => (a >= 0 && a < (Int.MaxValue-1)/2 && b > 0 && b < (Int.MaxValue-1)/2) ==> {
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
    "correctly parse noise in DM's decisions" in {
      val s1 = mock[EvaluatedSolution]
      val s2 = mock[EvaluatedSolution]
      val s3 = mock[EvaluatedSolution]
      val s4 = mock[EvaluatedSolution]
      val s5 = mock[EvaluatedSolution]
      val s6 = mock[EvaluatedSolution]
      val s7 = mock[EvaluatedSolution]
      val s8 = mock[EvaluatedSolution]
      val s9 = mock[EvaluatedSolution]
      val s10 = mock[EvaluatedSolution]
      val l = List(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10)
      forAll { (a: Int, b: Int) => (a > 0 && b >= 0) ==> {
        val left = l take (a+b)
        val right = l drop (a+b)
        val r = dm.addNoise(l, a, b)
        
        val rleft = r take (a+b)
        val rright = r drop (a+b)
        
        val ll = left.filterNot(i => rleft.contains(i))
        val rr = right.filterNot(i => rright.contains(i))
  
        ll.length == 0 && rr.length == 0
      }} must pass

      // Check if something really change
      var changes: Int = 0
      for (idx <- 1 to 20) {
        val ll = dm.addNoise(l, 3, 3)
        for (i <- 0 to 2) {
          if (l(i) != ll(i)) changes += 1
        }
        for (i <- 6 to 9) {
          l(i) must be_==(ll(i))
        }
      }
      changes must be_>(40)
      println(changes)
    }
  }
  
}
