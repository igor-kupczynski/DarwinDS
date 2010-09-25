package pl.poznan.put.darwin.gui

import pl.poznan.put.darwin.model.Config
import org.ini4j.ConfigParser
import swing._
import event._

object ConfigDialog {
  def show(window: Window, default: Config): Config = {
    val cc = if (default != null) Config.preconfParser(default) else Config.parser
    val d = new ConfigDialog(window, cc)
    d.setLocationRelativeTo(window)
    d.visible = true
    d.config
  }
}

class ConfigDialog(window: Window, val default: ConfigParser)
        extends Dialog(window) {

  visible = false
  modal = true

  val defaultConfig: Config = new Config(default)
  var config = defaultConfig
  val parser = Config.preconfParser(defaultConfig)

  private val btnSave = new Button("Save")
  private val btnCancel = new Button("Cancel")

  private val tabs = new TabbedPane
  tabs.pages += new TabbedPane.Page("Main", ConfigTab.mainTab)
  tabs.pages += new TabbedPane.Page("Algorithm", ConfigTab.algorithmTab)
  tabs.pages += new TabbedPane.Page("Fine Tuning", ConfigTab.finetuneTab)

  contents = new BoxPanel(Orientation.Vertical) {
    contents += tabs
    contents += new FlowPanel {
      contents += btnSave
      contents += btnCancel
    }
  }

  listenTo(btnSave, btnCancel)

  reactions += {
    case ButtonClicked(`btnSave`) => {
      config = new Config(parser)
      visible = false
    }

    case ButtonClicked(`btnCancel`) => {
      config = defaultConfig
      visible = false
    }
  }
}

object ConfigTab {

  def mainTab: Panel = {
    val w = List(
      ("main", "solutioncount", "Number of solutions", new TextField("30")),
      ("main", "scenariocount", "Number of scenarios", new TextField("30")),
      ("main", "generationcount", "Number of generations", new TextField("30")),
      ("main", "percentiles", "Percentiles", new TextField("1.0, 25.0, 50.0")),
      ("main", "useavg", "Use average in quantiles", new CheckBox())
      )
    create(w)
  }

  def algorithmTab = create(List(
    ("algo", "allrules", "Use all rules instead of DomLEM", new CheckBox()),
    ("algo", "domlemconfidencelevel", "DomLEM confidence level", new TextField("1.0")),
    ("evolution", "compareusingsupposedutility", "Compare using supposed utility", new CheckBox())
  ))

  def finetuneTab = create(List(
      ("main", "delta", "Delta", new TextField("0.1")),
      ("main", "gamma", "Gamma", new TextField("2.0")),
      ("main", "eta", "Eta", new TextField("0.5")),
      ("main", "omega", "Omega", new TextField("0.1"))
  ))

  def create(widgets: List[(String, String, String, Component)]): Panel = {
    val p = new GridPanel(0, 2)
      widgets.foreach( { case (sec, name, label, comp) => {
        p.contents += new Label(label)
        p.contents += comp
      }})
    p
  }
}