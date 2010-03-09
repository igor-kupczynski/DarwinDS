package pl.poznan.put.darwin.model

import pl.poznan.put.darwin.model.Config.{Scenario, Solution}
import collection.mutable.HashMap

case class Problem(name: String, goals: List[Goal], constraints: List[Expression]) {
  private var variables: List[Variable] = null
  private var intervals: List[Interval] = null

  def getVariables(): List[Variable] = {
    if (variables == null) {
      variables = Nil
      goals foreach ((g: Goal) => {
        ExpressionExtractor.getVariables(g.exp) foreach ((v: Variable) => {
          if (!variables.contains(v)) variables = v :: variables
        })
      })
      constraints foreach ((e: Expression) => {
        ExpressionExtractor.getVariables(e) foreach ((v: Variable) => {
          if (!variables.contains(v)) variables = v :: variables
        })
      })
    }
    variables
  }

  def getIntervals(): List[Interval] = {
    if (intervals == null) {
      intervals = Nil
      goals foreach ((g: Goal) => {
        ExpressionExtractor.getIntervals(g.exp) foreach ((i: Interval) => {
          if (!intervals.contains(i)) intervals = i :: intervals
        })
      })
      constraints foreach ((e: Expression) => {
        ExpressionExtractor.getIntervals(e) foreach ((i: Interval) => {
          if (!intervals.contains(i)) intervals = i :: intervals
        })
      })
    }
    intervals
  }

  def evaluate(scenario: Scenario, solution: Solution): HashMap[Goal, Double] = {
    val result = new HashMap[Goal, Double]
    goals foreach ((g: Goal) => {
      result(g) = ExpressionEvaluator.evaluate(g.exp, scenario, solution)
    })
    result
  }
}


object DefaultProblemFactory {
  def getProblem(): Problem = {

    // Variables
    val xa = Variable("xa")
    val xb = Variable("xb")
    val xc = Variable("xc")

    // Coefficients

    // Price
    val pa = Interval("pa", 20, 24)
    val pb = Interval("pb", 30, 36)
    val pc = Interval("pc", 25, 30)

    // Time
    val ta = Interval("ta", 5, 6)
    val tb = Interval("tb", 8, 9.6)
    val tc = Interval("tc", 10, 12)

    // Raw materials
    val r1a = Interval("r1a", 1, 1.2)
    val r1b = Interval("r1b", 2, 2.4)
    val r1c = Interval("r1c", 0.75, 0.9)
    val r1p = Interval("r1p", 6, 7.2)

    val r2a = Interval("r2a", 0.5, 0.6)
    val r2b = Interval("r2b", 1, 1.2)
    val r2c = Interval("r2c", 0.5, 0.6)
    val r2p = Interval("r2p", 9, 9.6)

    // Demand
    val da = Interval("da", 10, 12)
    val db = Interval("db", 20, 24)
    val dc = Interval("dc", 10, 12)


    // Goals
    val maxProfit = Goal("Profit",
      Sum((pa * Min(da :: xa :: Nil)) ::
              (pb * Min(db :: xb :: Nil)) ::
              (pc * Min(dc :: xc :: Nil)) ::
              Minus(r1p * Sum((r1a * xa) :: (r1b * xb) :: (r1c * xc) :: Nil)) ::
              Minus(r2p * Sum((r2a * xa) :: (r2b * xb) :: (r2c * xc) :: Nil)) ::
              Nil),
      true)

    val minTime = Goal("Time",
      Sum((ta * xa) :: (tb * xb) :: (tc * xc) :: Nil),
      false)

    // Constraints

    // Market limits
    val mLimit1 = Sum(Minus(xa) :: Constant(12) :: Nil)
    val mLimit2 = Sum(Minus(xb) :: Constant(14) :: Nil)
    val mLimit3 = Sum(Minus(xc) :: Constant(12) :: Nil)

    Problem("Default problem", maxProfit :: minTime :: Nil, mLimit1 :: mLimit2 :: mLimit3 :: xa :: xb :: xc :: Nil)
  }
}