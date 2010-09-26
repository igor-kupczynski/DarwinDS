package pl.poznan.put.darwin.gui

import swing._
import event._
import pl.poznan.put.darwin.simulation._
import pl.poznan.put.darwin.model.solution._
import java.awt.Cursor
import collection.mutable.ArrayBuffer

class DarwinWindow(main: Window) extends BorderPanel {

  preferredSize = new Dimension(500, 80)
  
  val selectors = new Selectors(main)
  val controls = new Controls
  var sim: Simulation = null

  val history = new ArrayBuffer[List[EvaluatedSolution]]

  private var running = false
  
  layout(selectors) = BorderPanel.Position.Center
  add(controls, BorderPanel.Position.South)

  listenTo(controls.components : _*)

  reactions += {
    case ButtonClicked(controls.solve) if (!running) => {
      if (selectors.hasProblem) {
        running = true
        controls.solve.enabled = false
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
      prerun()
      evaluated = sim.run(marked)
      history.append(evaluated)
      postrun()
      marked = DarwinDialog.show(main, sim, history)
      println(marked)
      if (marked == null) {
        System.exit(0)
      }
    }
  }

  private def prerun() {
    cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
  }

  private def postrun() {
    cursor = Cursor.getDefaultCursor
  }
}
  
  
class Controls extends FlowPanel {

  val solve = new Button("Solve")
  def components: List[Publisher] = {solve :: Nil}
  contents += solve
}

