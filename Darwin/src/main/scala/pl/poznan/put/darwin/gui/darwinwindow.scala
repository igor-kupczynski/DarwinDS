package pl.poznan.put.darwin.gui

import swing._
import event._

class DarwinWindow extends BorderPanel {

  preferredSize = new Dimension(640, 480)
  
  val selectors = new Selectors
  val controls = new Controls

  private var running = false
  
  layout(selectors) = BorderPanel.Position.Center
  add(controls, BorderPanel.Position.South)

  listenTo(controls.components : _*)

  reactions += {
    case ButtonClicked(controls.solve) if (!running) => {
      val sim = selectors.newSim
      val config = sim.config
      revalidate()
      repaint()
      running = true
    }
  }

}
  
  
class Controls extends FlowPanel {

  val solve = new Button("Solve")
  def components: List[Publisher] = {solve :: Nil}
  contents += solve
}
