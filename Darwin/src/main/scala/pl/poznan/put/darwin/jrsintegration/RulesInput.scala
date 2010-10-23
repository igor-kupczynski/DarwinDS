package pl.poznan.put.darwin.jrsintegration

import pl.poznan.put.cs.idss.jrs.core.SerialInput
import pl.poznan.put.cs.idss.jrs.types._
import pl.poznan.put.darwin.model.problem.Goal
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.solution.MarkedSolution
import pl.poznan.put.darwin.utils.ListUtils

class RulesInput(result: List[MarkedSolution], randomize: Boolean) extends SerialInput {
  private val sim = result(0).sim

  private val enumDomain: EnumDomain = new EnumDomain() {
    addElement("NOT_GOOD")
    addElement("GOOD")
  }

  private val order = createIterationOrder(randomize)

  private var metadata: Metadata = createMetadata()
  private var examples: List[Example] = createExamples()

  override def inputExample(): Example = {
    if (examples.length > 0) {
      val head = examples.head
      examples = examples.tail
      head
    } else {
      return null
    }
  }

  override def inputMetadata(): Metadata = {
    metadata
  }

  private def createIterationOrder(randomize: Boolean): List[Tuple2[Goal, Int]] = {
    var order: List[Tuple2[Goal, Int]] = Nil
    result(0).goals foreach ((g: Goal) => {
      sim.config.PERCENTILES foreach (p => order = order :+ (g, p))
    })
    if (randomize) ListUtils.shuffle(order) else order
  }
  
  private def createExamples(): List[Example] = {
    var examples: List[Example] = Nil
    result foreach (s => examples = ExampleFactory(s, enumDomain, order) :: examples)
    examples.reverse
  }

  private def createMetadata(): Metadata = {
    var attributes: List[Attribute] = Nil
    var desc = new Attribute("Name", new StringField(""))
    desc.setPreferenceType(Attribute.GAIN)
    desc.setKind(Attribute.DESCRIPTION)
    desc.setActive(true)
    desc.setMembership(false)
    desc.setDiscretization(null)
    attributes = desc :: attributes

    order foreach {case (g, p) => {
        val attribute = new Attribute(
          "%s_%d" format (g.name, p.toInt), new FloatField())
        attribute.setPreferenceType(if (g.max) Attribute.GAIN else Attribute.COST)
        attribute.setKind(Attribute.NONE)
        attribute.setActive(true)
        attribute.setMembership(false)
        attribute.setDiscretization(null)
        attributes = attribute :: attributes
      }
    }

    var dec = new Attribute("GOOD", new EnumField(0, enumDomain));
    dec.setPreferenceType(Attribute.GAIN)
    dec.setKind(Attribute.DECISION)
    dec.setActive(true)
    dec.setMembership(false)
    dec.setDiscretization(null)
    attributes = dec :: attributes
    attributes = attributes.reverse
    new Metadata(attributes.toArray[Attribute], null)
  }

}
