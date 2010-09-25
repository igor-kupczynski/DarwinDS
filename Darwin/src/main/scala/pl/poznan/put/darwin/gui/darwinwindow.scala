package pl.poznan.put.darwin.gui

import swing._
import event._
import pl.poznan.put.darwin.simulation._
import pl.poznan.put.darwin.model.solution._

class DarwinWindow(main: Window) extends BorderPanel {

  preferredSize = new Dimension(500, 80)
  
  val selectors = new Selectors(main)
  val controls = new Controls
  var sim: Simulation = null

  private var running = false
  
  layout(selectors) = BorderPanel.Position.Center
  add(controls, BorderPanel.Position.South)

  listenTo(controls.components : _*)

  reactions += {
    case ButtonClicked(controls.solve) if (!running) => {
      if (selectors.hasProblem) {
        running = true
        runSim()
      }
      else {
        Dialog.showMessage(this, "Select a problem first", "No problem selected", Dialog.Message.Error) 
      }
    }
  }

  private def runSim() = {
    sim = selectors.newSim
    var evaluated: List[EvaluatedSolution] = null
    var marked: List[MarkedSolution] = null
    while (true) {
      evaluated = sim.run(marked)
      marked = DarwinDialog.show(main, sim, evaluated)
      if (marked == null) {
        System.exit(0)
      }
    }
  }
}
  
  
class Controls extends FlowPanel {

  val solve = new Button("Solve")
  def components: List[Publisher] = {solve :: Nil}
  contents += solve
}

