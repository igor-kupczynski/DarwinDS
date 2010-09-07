package pl.poznan.put.darwin.gui

import swing._
import event._
import pl.poznan.put.darwin.simulation._
import pl.poznan.put.darwin.model.solution._
import javax.swing.JOptionPane

class DarwinWindow(main: Window) extends BorderPanel {

  preferredSize = new Dimension(320, 240)
  
  val selectors = new Selectors
  val controls = new Controls
  var sim: Simulation = null

  private var running = false
  
  layout(selectors) = BorderPanel.Position.Center
  add(controls, BorderPanel.Position.South)

  listenTo(controls.components : _*)

  reactions += {
    case ButtonClicked(controls.solve) if (!running) => {
      running = true
      runSim()
    }
  }

  private def runSim() = {
    sim = selectors.newSim
    var evaluated: List[EvaluatedSolution] = null
    var marked: List[MarkedSolution] = null
    while (true) {
      evaluated = sim.run(marked)
      println(evaluated)
      marked = DarwinDialog.show(main, sim, evaluated)
    }
  }
}
  
  
class Controls extends FlowPanel {

  val solve = new Button("Solve")
  def components: List[Publisher] = {solve :: Nil}
  contents += solve
}

object DarwinDialog {
  def show(window: Window,
           sim: Simulation,
           evaluated: List[EvaluatedSolution]): List[MarkedSolution] = {
    val d = new DarwinDialog(window, sim, evaluated)
    d.setLocationRelativeTo(window)
    d.visible = true
    d.marked
  }
       
}
  
class DarwinDialog(window: Window, val sim: Simulation,
                   val evaluated: List[EvaluatedSolution])
    extends Dialog(window) {
  visible = false
  modal = true
  private val btn = new Button("Mark")
  
  contents = new FlowPanel {
    contents += new Label("foo")
    contents += btn
  }

  var marked: List[MarkedSolution] = null
  
  listenTo(btn)

  reactions += {
    case ButtonClicked(btn) => {
      var idx = 0
      marked = evaluated.map(e => {
        idx += 1
        if (idx < 5)
          MarkedSolution(e, true)
        else
          MarkedSolution(e, false)
      })
      visible = false
    }
  }
}
