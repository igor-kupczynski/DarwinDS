package pl.poznan.put.darwin.gui

import swing._
import event._
import pl.poznan.put.darwin.simulation._
import pl.poznan.put.darwin.model.solution._
import java.awt.Cursor
import collection.mutable.ArrayBuffer
import javax.swing.{SwingWorker => JSW}

class DarwinWindow(main: Window) extends BorderPanel {

  preferredSize = new Dimension(500, 80)
  
  val selectors = new Selectors(main)
  val controls = new Controls
  var sim: Simulation = null

  private var running = false

  private var history: ArrayBuffer[List[EvaluatedSolution]] = null

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
    history = new ArrayBuffer[List[EvaluatedSolution]]
    sim = selectors.newSim
    this.cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
    val evaluated = sim.run(null)
    nextIter(evaluated)
  }


  def nextIter(evaluated: List[EvaluatedSolution]) {
    this.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
    history.append(evaluated)
    var marked = DarwinDialog.show(main, sim, history)
    if (marked == null) {
      System.exit(0)
    }
    if (marked.filter({_.good}).length == 0) {
      running = false
      controls.solve.enabled = true
    }
    (new SimRunner(this, marked)).execute
    this.cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
  }
}
  
class Controls extends FlowPanel {

  val solve = new Button("Solve")

  def components: List[Publisher] = {solve :: Nil}
  contents += solve
}

class SimRunner(dW: DarwinWindow,
                marked: List[MarkedSolution]) extends JSW[List[EvaluatedSolution], Any] {

  def doInBackground: scala.List[EvaluatedSolution] = {
    dW.sim.run(marked)
  }


  override def done: Unit = {
    try {
      val evaluated = get()
      dW.nextIter(evaluated)
    } catch {
      case e: Exception => e.printStackTrace
    }
  }
}