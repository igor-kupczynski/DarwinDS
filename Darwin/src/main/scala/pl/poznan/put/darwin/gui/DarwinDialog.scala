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
  private val btnMark = new Button("Mark")
  private val btnShow = new Button("Show")

  val table = new Table(rowData, columnNames)
  
  contents = new BoxPanel(Orientation.Vertical) {
    contents += new ScrollPane(table)
    contents += new FlowPanel {
      contents += btnMark
      contents += btnShow
    }
  }

  var marked: List[MarkedSolution] = null
  
  listenTo(btnMark, btnShow)

  reactions += {
    case ButtonClicked(`btnMark`) => {
      marked = markSelected
      visible = false
    }

    case ButtonClicked(`btnShow`) => {
      var msg: String = ""
      for (r <- table.selection.rows) {
        var row = ">>> Solution %02d:\n" format r
        val s: EvaluatedSolution = evaluated(r)
        for ((k, v) <- s.values) {
          row = row + ("  %s = %s\n" format (k, v))
        }
        msg = msg + row + "\n"
      }
      Dialog.showMessage(table, msg)
    }
  }

  private def markSelected(): List[MarkedSolution] = {
    val m = table.model
    var idx = -1
    evaluated.map(e => {
      idx += 1
      val good: Boolean = (m.getValueAt(idx, 1)).asInstanceOf[Boolean]
      MarkedSolution(e, good)
    })
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
    result
  }
}

