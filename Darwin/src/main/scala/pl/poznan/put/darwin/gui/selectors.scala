package pl.poznan.put.darwin.gui

import swing._
import event._
import java.io.File
import org.ini4j.ConfigParser
import pl.poznan.put.darwin.model.Config
import pl.poznan.put.darwin.model.problem.{Parser, Problem}
import pl.poznan.put.darwin.simulation.Simulation

class Selectors extends GridPanel(2, 3) {
  private var configFileName = "--- empty ---"
  private var problemFileName = "--- empty ---"

  val configFileLabel = new Label(configFileName)
  val problemFileLabel = new Label(problemFileName)

  val configButton = new Button("Open")
  val problemButton = new Button("Open")
  
  val chooser = new FileChooser
  
  contents += new Label("Problem")
  contents += configFileLabel
  contents += configButton
  contents += new Label("Parameters")
  contents += problemFileLabel
  contents += problemButton

  border = Swing.EmptyBorder(5, 5, 5, 5)

  listenTo(configButton, problemButton)

  private var config: Config = null
  private var problem: Problem = null
   
  def newSim: Simulation = {
    new Simulation(config, problem)
  }

  
  reactions += {
    case ButtonClicked(this.configButton) => {
      val rc = chooser.showOpenDialog(this)
      if (rc == FileChooser.Result.Approve) {
        var file = chooser.selectedFile
        configFileName = chooser.nameFor(file)
        configFileLabel.text = configFileName
        val parser = new ConfigParser()
        parser.read(file)
        config = new Config(parser)
      }
    }
    case ButtonClicked(this.problemButton) => {
      val rc = chooser.showOpenDialog(this)
      if (rc == FileChooser.Result.Approve) {
        var file = chooser.selectedFile
        problemFileName = chooser.nameFor(file)
        problemFileLabel.text = problemFileName
        val lines = io.Source.fromFile(file).mkString
        problem = Parser.fromText(lines)
      }
    }
  }
}
