package pl.poznan.put.darwin.gui

import swing._
import event._
import pl.poznan.put.darwin.simulation._
import pl.poznan.put.darwin.model.solution._

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
  
  contents = new BoxPanel(Orientation.Vertical) {
    contents += new ScrollPane(new Table(rowData, columnNames))
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

  private def rowData(): Array[Array[Any]] = {
    val cols = 2 + sim.problem.goals.length * sim.config.PERCENTILES.length
    val rows = evaluated.length
    var result: Array[Array[Any]] = Array.ofDim(rows, cols)

    var rowIdx = 0
    var colIdx = 0
    for (e <- evaluated) {
      result(rowIdx)(0) = rowIdx
      result(rowIdx)(1) = false
      colIdx = 2
      for (g <- sim.problem.goals) {
        for (p <- sim.config.PERCENTILES) {
          result(rowIdx)(colIdx) = e.getPercentile(g, p)
          colIdx += 1
        }
      }
      rowIdx += 1
    }
    result
  }
  
  private def columnNames(): List[String] = {
    var result: List[String] = List("id", "mark")
    for (g <- sim.problem.goals) {
      for (p <- sim.config.PERCENTILES) {
        result = result :+ ("%s_%s" format (g.name, p))
      }
    }
    println(result)
    result
  }
}

