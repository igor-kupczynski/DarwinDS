package pl.poznan.put.darwin.model

import org.specs.Specification

class ConfigTest extends Specification {

  val defConf = new Config(Config.parser)

  "Config" should {
    "correctly pass the paramters" in {
      val defConf2 = new Config(Config.preconfParser(defConf))
      defConf2.PERCENTILES must be_==(defConf.PERCENTILES)
      val p3 = Config.parser
      p3.set("main", "percentiles", "1.0, 2.0, 3.0")
      val conf3 = new Config(p3)
      conf3.PERCENTILES must haveTheSameElementsAs(List(1.0, 2.0, 3.0))
      val p4 = Config.preconfParser(conf3)
      p4.get("main", "percentiles") must be_==("1.0, 2.0, 3.0")
    }

  }
}