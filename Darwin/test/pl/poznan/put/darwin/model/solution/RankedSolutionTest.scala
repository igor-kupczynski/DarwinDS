package pl.poznan.put.darwin.model.solution

import org.specs._
import mock.Mockito
import org.mockito.Matchers._

class RankedSolutionTest extends SpecificationWithJUnit with Mockito {

  val m = mock[java.util.List[String]]

  m.get(anyInt()) returns "one"

  "mock" should {
    "return 'one' when asked" in {
      m.get(1) must be_==("one")
    }
  }

}