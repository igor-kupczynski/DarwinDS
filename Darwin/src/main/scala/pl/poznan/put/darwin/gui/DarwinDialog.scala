package pl.poznan.put.darwin.gui

import swing._
import event._
import pl.poznan.put.darwin.simulation._
import pl.poznan.put.darwin.model.solution._
import javax.swing.JTable
import javax.swing.table.AbstractTableModel
import java.lang.{Class, String}

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
  private val btnRef = new Button("Refresh")

  val jTable: JTable = new JTable(new DarwinTableModel)
  jTable.setAutoCreateRowSorter(true)
  val table: Component = new Component {
    override lazy val peer = jTable
  }
  
  contents = new BoxPanel(Orientation.Vertical) {
    contents += new ScrollPane(table)
    contents += new FlowPanel {
      contents += btnMark
      contents += btnShow
      contents += btnRef
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

      for (r <- jTable.getSelectedRows) {
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
    val m = jTable.getModel
    var idx = -1
    evaluated.map(e => {
      idx += 1
      val good: Boolean = (m.getValueAt(idx, 1)).asInstanceOf[Boolean]
      MarkedSolution(e, good)
    })
  }
  


  class DarwinTableModel extends AbstractTableModel {

    val columnNames = genColumnNames
    val rowData = genRowData

    def getColumnCount: Int = columnNames.size
    def getRowCount: Int = rowData.size
    def getValueAt(rowIndex: Int, columnIndex: Int): AnyRef =
      rowData.get(rowIndex).get(columnIndex)

    override def getColumnName(column: Int): String = columnNames.get(column)
    override def getColumnClass(columnIndex: Int): Class[_] = getValueAt(0, columnIndex).getClass

    override def isCellEditable(rowIndex: Int, columnIndex: Int): Boolean =
      if (columnIndex == 1) true else false
    override def setValueAt(aValue: AnyRef, rowIndex: Int, columnIndex: Int): Unit = {
      rowData.get(rowIndex).set(columnIndex, aValue)
    }

    private def genColumnNames(): java.util.Vector[String] = {
      var result: java.util.Vector[String] = new java.util.Vector()
      result.add("Id")
      result.add("Is good?")
      for (g <- sim.problem.goals) {
        for (p <- sim.config.PERCENTILES) {
          result.add("%s_%s" format (g.name, p))
        }
      }
      result
    }

    private def genRowData(): java.util.Vector[java.util.Vector[Object]] = {
      val result: java.util.Vector[java.util.Vector[Object]] = new java.util.Vector()

      var rowIdx = 0
      for (e <- evaluated) {
        val row: java.util.Vector[Object] = new java.util.Vector()

        row.add(rowIdx.asInstanceOf[Object])
        row.add(false.asInstanceOf[Object])
        for (g <- sim.problem.goals) {
          for (p <- sim.config.PERCENTILES) {
            row.add(e.getPercentile(g, p).asInstanceOf[Object])
          }
        }
        result.add(row)
        rowIdx += 1
      }
      result
    }

  }
}

