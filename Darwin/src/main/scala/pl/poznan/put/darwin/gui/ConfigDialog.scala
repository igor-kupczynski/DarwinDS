package pl.poznan.put.darwin.gui

import pl.poznan.put.darwin.model.Config
import org.ini4j.ConfigParser
import swing._
import event._
import java.io.File

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
  tabs.pages += new TabbedPane.Page("Main", ConfigTab.mainTab(parser))
  tabs.pages += new TabbedPane.Page("Algorithm", ConfigTab.algorithmTab(parser))
  tabs.pages += new TabbedPane.Page("Fine Tuning", ConfigTab.finetuneTab(parser))
  tabs.pages += new TabbedPane.Page("Reports", ConfigTab.reportsTab(parser))

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
      try {
        config = new Config(parser)
        visible = false
      } catch {
        case e: NumberFormatException =>
          Dialog.showMessage(tabs, "%s" format (e.getMessage),
            "Wrong number format",
            Dialog.Message.Error)
      }
    }

    case ButtonClicked(`btnCancel`) => {
      config = defaultConfig
      visible = false
    }
  }
}

object ConfigTab {

  private def cb(p: ConfigParser, ss: String, nn: String) = {
    val cc = new CheckBox()
    cc.selected = p.get(ss, nn) == "true"
    cc
  }

  def mainTab(p: ConfigParser): Panel = {
    val w = List(
      ("main", "solutioncount", "Number of solutions", new TextField(p.get("main", "solutioncount"))),
      ("main", "scenariocount", "Number of scenarios", new TextField(p.get("main", "scenariocount"))),
      ("main", "generationcount", "Number of generations", new TextField(p.get("main", "generationcount"))),
      ("main", "percentiles", "Percentiles", new TextField(p.get("main", "percentiles"))),
      ("main", "useavg", "Use average in quantiles", cb(p, "main", "useavg"))
      )
    create(w, p)
  }

  def algorithmTab(p: ConfigParser) = create(List(
    ("algo", "allrules", "Use all rules instead of DomLEM", cb(p, "algo", "allrules")),
    ("algo", "domlemconfidencelevel", "DomLEM confidence level", new TextField(p.get("algo", "domlemconfidencelevel"))),
    ("evolution", "compareusingsupposedutility", "Compare using supposed utility", cb(p, "evolution", "compareusingsupposedutility"))
  ), p)

  def finetuneTab(p: ConfigParser) = create(List(
      ("main", "delta", "Delta", new TextField(p.get("main", "delta"))),
      ("main", "gamma", "Gamma", new TextField(p.get("main", "gamma"))),
      ("main", "eta", "Eta", new TextField(p.get("main", "eta"))),
      ("main", "omega", "Omega", new TextField(p.get("main", "omega")))
  ), p)


  def reportsTab(parser: ConfigParser): Panel = {
    new GridPanel(5, 2) {
      val ev = parser.get("reports", "evolutionreport")
      val dm = parser.get("reports", "dmreport")

      var rules = parser.get("reports", "rulesdirectory")
      var reportPrefix = getReportPrefix(ev, dm)

      val chooser = new FileChooser(new File(reportPrefix))
      chooser.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly

      val rulesChooser = new FileChooser(new File(if (rules != "") rules else "."))
      rulesChooser.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly

      val evReportCb = new CheckBox()
      if (ev != "") {
        evReportCb.selected = true
        resetEvReport
      }
      val dmReportCb = new CheckBox()
      if (dm != "") {
        dmReportCb.selected = true
        resetDmReport
      }
      val rulesCb = new CheckBox()
      if (rules != "") {
        rulesCb.selected = true
      }

      val button = new Button("Change")
      val rulesButton = new Button("Change")

      contents += new Label("Report directory")
      contents += button
      contents += new Label("Save evolutionary report")
      contents += evReportCb
      contents += new Label("Save decision maker report")
      contents += dmReportCb
      contents += new Label("Rules directory")
      contents += rulesButton
      contents += new Label("Save rules")
      contents += rulesCb

      listenTo(evReportCb, dmReportCb, rulesCb, button, rulesButton)

      reactions += {
        case ButtonClicked(`evReportCb`) => resetEvReport
        case ButtonClicked(`dmReportCb`) => resetDmReport
        case ButtonClicked(`button`) => {
          val rc = chooser.showOpenDialog(this)
          if (rc == FileChooser.Result.Approve) {
            val file = chooser.selectedFile
            if (file.canWrite) {
              reportPrefix = file.getAbsolutePath
              resetEvReport
              resetDmReport
            } else {
              Dialog.showMessage(this, "Can not write to %s" format (file.getPath),
                "Wrong directory selected",
                Dialog.Message.Error)
            }
          }
        }
        case ButtonClicked(`rulesButton`) => {
          val rc = rulesChooser.showOpenDialog(this)
          if (rc == FileChooser.Result.Approve) {
            val file = rulesChooser.selectedFile
            if (file.canWrite) {
              rules = file.getAbsolutePath
              resetRules
            } else {
              Dialog.showMessage(this, "Can not write to %s" format (file.getPath),
                "Wrong directory selected",
                Dialog.Message.Error)
            }
          }
        }
      }

      private def resetEvReport {
        val evPath = if (evReportCb.selected) "%s/evolution_report.csv" format reportPrefix else ""
        println("ev <- %s" format evPath)
        parser.set("reports", "evolutionreport", evPath)
      }

      private def resetDmReport {
        val dmPath = if (dmReportCb.selected) "%s/dm_report.csv" format reportPrefix else ""
        println("dm <- %s" format dmPath)
        parser.set("reports", "dmreport", dmPath)
      }

      private def resetRules {
        val path = if (rulesCb.selected) rules else ""
        println("rules <- %s" format path)
        parser.set("reports", "rulesdirectory", path)
      }

      private def getReportPrefix(ev: String, dm: String): String = {
        if (ev != "") {
          (new File(ev).getParentFile.getAbsolutePath)
        } else if (dm != "") {
          (new File(dm).getParentFile.getAbsolutePath)
        } else "."
      }
    }
  }

  private def create(widgets: List[(String, String, String, Component)],
             parser: ConfigParser): Panel = {
    val p = new GridPanel(5, 2) {
      var configMap: Map[Component, (String, String)] = Map()
    }
    widgets.foreach( { case (sec, name, label, comp) => {
      p.configMap += (comp -> (sec, name))
      p.contents += new Label(label)
      p.contents += comp
      p.listenTo(comp)
      p.reactions += {
        case ValueChanged(`comp`) if (comp.isInstanceOf[TextComponent]) => {
          val tt = p.configMap(comp)
          val vv = comp.asInstanceOf[TextComponent].text
          parser.set(tt._1, tt._2, vv)
        }
        case ButtonClicked(`comp`) if comp.isInstanceOf[CheckBox] => {
          val tt = p.configMap(comp)
          val vv = if (comp.asInstanceOf[CheckBox].selected) "true" else "false"
          parser.set(tt._1, tt._2, vv)
        }
      }
    }})
    p
  }
}