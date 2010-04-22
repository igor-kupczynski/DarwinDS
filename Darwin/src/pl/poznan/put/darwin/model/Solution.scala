package pl.poznan.put.darwin.model

import collection.immutable.HashMap
import collection.mutable
import problem.{Evaluator, Goal, Problem}

object SolutionState extends Enumeration {
  type SolutionState = Value

  val CREATED = Value("CREATED")
  val EVALUATING = Value("EVALUATING")
  val EVALUATED = Value("EVALUATED")
  val MARKED = Value("MARKED")
  val SCORED = Value("SCORED")
  val FITNESS = Value("FITNESS")
}

import SolutionState._

/**
 * Class representing solution. Acts as state machine folwing processes of solution evaluation.
 *
 * @author: Igor Kupczynski
 */


