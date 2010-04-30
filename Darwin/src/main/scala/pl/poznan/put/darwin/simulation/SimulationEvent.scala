package pl.poznan.put.darwin.simulation

import pl.poznan.put.darwin.model.solution.{MarkedSolution, RankedSolution}

  
/**
 * Abstract class defining simulation event. Events are raisen when certain
 * type of situations in simulation occur.
 *
 * @author: Igor Kupczynski
 */
abstract class SimulationEvent


// --- Evolution events ---
  
/**
 * Abstract class for all evolution events
 *
 * @author: Igor Kupczynski
 */
abstract class EvolutionEvent extends SimulationEvent

/**
 * Event indicationg new generation in evolutionary loop
 *
 * @author: Igor Kupczynski
 */
case class NewGenerationEvent(solutions: List[RankedSolution]) extends SimulationEvent


// --- DM events ---

/**
 * Abstract base class for all DM events
 *
 * @author: Igor Kupczynski
 */
abstract class DMEvent extends SimulationEvent

/**
 * Event indicating choice of "GOOD" solutions by DM
 *
 * @author: Igor Kupczynski
 */
case class DMChoiceEvent(solutions: List[MarkedSolution]) extends DMEvent
