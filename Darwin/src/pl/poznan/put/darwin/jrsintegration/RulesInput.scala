package pl.poznan.put.darwin.jrsintegration

import pl.poznan.put.cs.idss.jrs.core.SerialInput
import pl.poznan.put.darwin.experiment.SolutionResult
import collection.mutable.HashMap
import pl.poznan.put.darwin.model.Config.Solution
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.cs.idss.jrs.types._
import pl.poznan.put.darwin.model.problem.Goal

class RulesInput(result: List[Tuple2[Solution, SolutionResult]]) extends SerialInput {
  private val enumDomain: EnumDomain = new EnumDomain() {
    addElement("NOT_GOOD")
    addElement("GOOD")
  }

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

  private def createExamples(): List[Example] = {
    var examples: List[Example] = Nil
    result foreach {case (_, sr: SolutionResult) => {
      examples = createExample(sr) :: examples
    }}
    examples.reverse
  }

  private def createExample(sr: SolutionResult): Example = {
    var fields: List[Field] = SolutionConverter.getFields(sr)
    val decision = new EnumField(if (sr.isGood) 1 else 0, enumDomain)
    fields = fields ::: List(decision)
    new Example(fields.toArray)
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

    val anyResult = result(0)._2
    anyResult.goals foreach ((g: Goal) => {
      Config.PERCENTILES foreach (p => {
        val attribute = new Attribute(
          g.name + "_" + p, new FloatField())
        attribute.setPreferenceType(if (g.max) Attribute.GAIN else Attribute.COST)
        attribute.setKind(Attribute.NONE)
        attribute.setActive(true)
        attribute.setMembership(false)
        attribute.setDiscretization(null)
        attributes = attribute :: attributes
      })
    })

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