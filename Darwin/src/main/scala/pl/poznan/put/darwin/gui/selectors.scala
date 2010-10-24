package pl.poznan.put.darwin.gui

import swing._
import event._
import org.ini4j.ConfigParser
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.{Parser, Problem}
import java.io.{FileOutputStream, File}
import pl.poznan.put.darwin.simulation.{FullEvoReportGenerator, DMReportGenerator, EvolutionReportGenerator, Simulation}
import com.weiglewilczek.slf4s.Logging

class Selectors(main: Window) extends GridPanel(1, 4) with Logging {
  hGap = 3
  vGap = 3

  private var problemFileName = "--- empty ---"

  val problemFileLabel = new Label(problemFileName)

  val configButton = new Button("Options")
  val problemButton = new Button("Open")
  
  val chooser = new FileChooser(new File ("."))
  
  contents += new Label("Problem")
  contents += problemFileLabel
  contents += problemButton
  contents += configButton

  border = Swing.EmptyBorder(2, 5, 2, 5)

  listenTo(configButton, problemButton)

  private var config: Config = _
  try {
    val parser = new ConfigParser()
    parser.read("etc/gui_config.ini")
    config = new Config(parser)
  } catch {
    case _ => {config = Config()}
  }

  private var problem: Problem = null

  def hasProblem = problem != null

  def newSim: Simulation = {
    config.problem_name = problemFileName
    val sim = new Simulation(config, problem)
    if (config.EVOLUTION_REPORT != ""  )
      sim.registerObserver(
        new EvolutionReportGenerator(sim,
          new FileOutputStream(config.EVOLUTION_REPORT)))
    if (config.DM_REPORT != "")
      sim.registerObserver(
        new DMReportGenerator(sim,
          new FileOutputStream(config.DM_REPORT)))
//    sim.registerObserver(new FullEvoReportGenerator(sim,
//           new FileOutputStream("./reports/evofull.csv")))
    sim
  }

  
  reactions += {
    case ButtonClicked(this.configButton) => {
      config = ConfigDialog.show(main, config)
    }
    case ButtonClicked(this.problemButton) => {
      val rc = chooser.showOpenDialog(this)
      if (rc == FileChooser.Result.Approve) {
        val file = chooser.selectedFile
        problemFileName = chooser.nameFor(file)
        problemFileLabel.text = problemFileName
        val lines = io.Source.fromFile(file).mkString
        logger info "Opening %s".format(problemFileName)
        problem = Parser.fromText(lines)
        logger info "Opened %s".format(problemFileName)
      }
    }
  }
}
