package pl.poznan.put.darwin.model.solution

import pl.poznan.put.darwin.ProblemRepository
import org.specs.{Specification}

class SolutionTest extends Specification with ProblemRepository  {

  "Solution" should {
    "remember values stored in it" in {
      val s = Solution(trainsSoldiersNoIntervalsSim, Map("x1" -> 10.0, "x2" -> 20.0))
      s.sim must be_==(trainsSoldiersNoIntervalsSim)
      s.values must be_==(Map("x1" -> 10.0, "x2" -> 20.0))
    }
    "should generate feasible random solutions" in {
      for (idx <- 1 to 50) {
        val r = Solution.random(trainsSoldiersNoIntervalsSim)
        r.sim must be_==(trainsSoldiersNoIntervalsSim)
        val x1 = r.values("x1")
        x1 must be_>=(0.0)
        x1 must be_<=(200.0)
        val x2 = r.values("x2")
        x2 must be_>=(0.0)
        x2 must be_<=(200.0)
      }
    }
    "respect integer constraints" in {
      for (idx <- 1 to 50) {
        val r = Solution.random(integerTrainsSoldiersNoIntervalsSim)
        r.sim must be_==(integerTrainsSoldiersNoIntervalsSim)
        val x1 = r.values("x1")
        x1 must be_>=(0.0)
        x1 must be_<=(200.0)
        x1 must be_==(math.round(x1))
        val x2 = r.values("x2")
        x2 must be_>=(0.0)
        x2 must be_<=(200.0)
        x2 must be_==(math.round(x2))
      }
    }
  "respect binary constraints" in {
      for (idx <- 1 to 50) {
        val r = Solution.random(binarySimpleNoIntervalsSim)
        r.sim must be_==(binarySimpleNoIntervalsSim)
        val x = r.values("x")
        x must beOneOf(0.0, 1.0)
      }
    }
    "correctly evaluate feasibility of itself" in {
      (new Solution(simpleNoIntervalsSim, Map("x" -> -199.0))).isFeasible must be(false)
      (new Solution(simpleNoIntervalsSim, Map("x" ->   -1.0))).isFeasible must be(false)
      (new Solution(simpleNoIntervalsSim, Map("x" ->    0.0))).isFeasible must be(true)
      (new Solution(simpleNoIntervalsSim, Map("x" ->    1.0))).isFeasible must be(true)
      (new Solution(simpleNoIntervalsSim, Map("x" ->   50.0))).isFeasible must be(true)
      (new Solution(simpleNoIntervalsSim, Map("x" ->   99.0))).isFeasible must be(true)
      (new Solution(simpleNoIntervalsSim, Map("x" ->  100.0))).isFeasible must be(true)
      (new Solution(simpleNoIntervalsSim, Map("x" ->  101.0))).isFeasible must be(false)
      (new Solution(simpleNoIntervalsSim, Map("x" ->  199.0))).isFeasible must be(false)
      (new Solution(simpleNoIntervalsSim, Map("x" ->  400.0))).isFeasible must be(false)

      (new Solution(trainsSoldiersNoIntervalsSim,
                    Map("x1" -> -1.0, "x2" -> -1.0))).isFeasible must be(false)
      (new Solution(trainsSoldiersNoIntervalsSim,
                    Map("x1" ->  1.0, "x2" ->  1.0))).isFeasible must be(true)
      (new Solution(trainsSoldiersNoIntervalsSim,
                    Map("x1" -> 40.0, "x2" ->  1.0))).isFeasible must be(true)
      (new Solution(trainsSoldiersNoIntervalsSim,
                    Map("x1" -> 41.0, "x2" ->  1.0))).isFeasible must be(false)
      (new Solution(trainsSoldiersNoIntervalsSim,
                    Map("x1" ->198.0, "x2" ->168.0))).isFeasible must be(false)

      (new Solution(integerTrainsSoldiersNoIntervalsSim,
                    Map("x1" -> -1.0, "x2" -> -1.0))).isFeasible must be(false)
      (new Solution(integerTrainsSoldiersNoIntervalsSim,
                    Map("x1" ->  1.0, "x2" ->  1.0))).isFeasible must be(true)
      (new Solution(integerTrainsSoldiersNoIntervalsSim,
                    Map("x1" ->  1.5, "x2" ->  1.0))).isFeasible must be(false)
      (new Solution(integerTrainsSoldiersNoIntervalsSim,
                    Map("x1" ->  1.0, "x2" ->  1.7))).isFeasible must be(false)
      (new Solution(integerTrainsSoldiersNoIntervalsSim,
                    Map("x1" -> 40.0, "x2" ->  1.0))).isFeasible must be(true)
      (new Solution(integerTrainsSoldiersNoIntervalsSim,
                    Map("x1" -> 41.0, "x2" ->  1.0))).isFeasible must be(false)
      (new Solution(integerTrainsSoldiersNoIntervalsSim,
                    Map("x1" ->198.0, "x2" ->168.0))).isFeasible must be(false)
  
      (new Solution(binarySimpleNoIntervalsSim,
                    Map("x" -> -1.0))).isFeasible must be(false)
      (new Solution(binarySimpleNoIntervalsSim,
                    Map("x" -> 0.0))).isFeasible must be(true)
      (new Solution(binarySimpleNoIntervalsSim,
                    Map("x" -> 1.0))).isFeasible must be(true)
      (new Solution(binarySimpleNoIntervalsSim,
                    Map("x" -> 0.5))).isFeasible must be(false)
      (new Solution(binarySimpleNoIntervalsSim,
                    Map("x" -> 220.0))).isFeasible must be(false)
    }
    "should generate feasible random neighbour" in {
      val s = Solution(trainsSoldiersNoIntervalsSim, Map("x1" -> 10.0, "x2" -> 20.0))
      for (idx <- 1 to 50) {
        val rn = s.randomNeighbour
        rn.isFeasible must be(true)
        rn must be_!=(s)
      }
    }
  }
}
