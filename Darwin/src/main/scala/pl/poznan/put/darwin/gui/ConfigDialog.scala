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
  tabs.pages += new TabbedPane.Page("main", ConfigTab.mainTab)

  contents = new BoxPanel(Orientation.Vertical) {
    contents += new ScrollPane(tabs)
    contents += new FlowPanel {
      contents += btnSave
      contents += btnCancel
    }
  }

  listenTo(btnSave, btnCancel)

  reactions += {
    case ButtonClicked(`btnSave`) => {
      println("Save clicked!")
      config = new Config(parser)
      visible = false
    }

    case ButtonClicked(`btnCancel`) => {
      println("Cancel clicked!")
      config = defaultConfig
      visible = false
    }
  }
}

object ConfigTab {

  def mainTab: Panel = {
    val w = List(
      ("main", "debug", "Debug mode", new TextField("false"))  
    )
    create(w)
  }

  def create(widgets: List[(String, String, String, Component)]): Panel = {
    val p = new BoxPanel(Orientation.Vertical)
      widgets.foreach({p.contents += _._4})
    p
  }
}