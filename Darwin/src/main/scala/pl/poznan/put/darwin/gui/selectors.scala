package pl.poznan.put.darwin.gui

import swing._
import event._
import java.io.File
import org.ini4j.ConfigParser
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.{Parser, Problem}
import pl.poznan.put.darwin.simulation.Simulation

class Selectors(main: Window) extends GridPanel(1, 4) {
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

  private var config: Config = Config()
  private var problem: Problem = null

  def hasProblem = problem != null

  def newSim: Simulation = {
    new Simulation(config, problem)
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
        problem = Parser.fromText(lines)
      }
    }
  }
}
