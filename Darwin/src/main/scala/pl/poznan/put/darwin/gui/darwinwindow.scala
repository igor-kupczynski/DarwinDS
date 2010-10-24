package pl.poznan.put.darwin.gui

import swing._
import event._
import pl.poznan.put.darwin.simulation._
import pl.poznan.put.darwin.model.solution._
import java.awt.Cursor
import collection.mutable.ArrayBuffer
import javax.swing.{SwingWorker => JSW}
import com.weiglewilczek.slf4s.Logging

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
    markBusy
    val evaluated = sim.run(null)
    nextIter(evaluated)
  }


  def nextIter(evaluated: List[EvaluatedSolution]) {
    markNotBusy
    history.append(evaluated)
    var marked = DarwinDialog.show(main, sim, history)
    if (marked == null) {
      System.exit(0)
    }
    sim.postDMChoices(marked)
    if (marked.filter({_.good}).length == 0) {
      running = false
      controls.solve.enabled = true
      markNotBusy
    } else {
      (new SimRunner(this, marked)).execute
      markBusy
    }
  }

  def markBusy = {
    this.cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
    controls.solve.visible = false
    controls.progressBar.visible = true
  }

  def markNotBusy = {
    this.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
    controls.solve.visible = true
    controls.progressBar.visible = false
  }
}
  
class Controls extends FlowPanel {

  val solve = new Button("Solve")
  val progressBar = new ProgressBar
  progressBar.indeterminate = true
  progressBar.label = "                                           "
  progressBar.labelPainted = true
  

  def components: List[Publisher] = {solve :: progressBar :: Nil}
  contents += solve
  contents += progressBar

  progressBar.visible = false  
}

class SimRunner(dW: DarwinWindow,
                marked: List[MarkedSolution]) extends JSW[List[EvaluatedSolution], Any]
    with Logging {

  def doInBackground: scala.List[EvaluatedSolution] = {
    dW.sim.run(marked)
  }


  override def done: Unit = {
    try {
      val evaluated = get()
      dW.nextIter(evaluated)
    } catch {
      case e => {
        logger.error("An error occured", e)
        System.exit(-1)
      }
    }
  }
}