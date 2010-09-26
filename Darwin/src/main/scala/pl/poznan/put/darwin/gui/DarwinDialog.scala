package pl.poznan.put.darwin.gui

import swing._
import event._
import pl.poznan.put.darwin.simulation._
import pl.poznan.put.darwin.model.solution._
import javax.swing.JTable
import javax.swing.table.AbstractTableModel
import java.lang.{Class, String}
import collection.mutable.ArrayBuffer

object DarwinDialog {
  def show(window: Window,
           sim: Simulation,
           history: ArrayBuffer[List[EvaluatedSolution]]): List[MarkedSolution] = {
    val d = new DarwinDialog(window, sim, history)
    d.setLocationRelativeTo(window)
    d.visible = true
    d.marked
  }
}
  
class DarwinDialog(window: Window, val sim: Simulation,
                   history: ArrayBuffer[List[EvaluatedSolution]])
    extends Dialog(window) {

  var currentRun = history.size - 1
  var evaluated = history(currentRun)

  visible = false
  modal = true
  private val btnMark = new Button("Mark as good")
  private val btnShow = new Button("Solution details")

  private val btnPrev = new Button("Previous")
  private val btnNext = new Button("Next")
  private val histLabel = new Label()
  resetButtons

  var jTable: JTable = _
  var table: Component = _
  createTable
  
  val bp =  new BoxPanel(Orientation.Vertical) {
    contents += new FlowPanel {
      contents += new Label("History:")
      contents += btnPrev
      contents += histLabel
      contents += btnNext
    }
    contents += new ScrollPane(table)
    contents += new FlowPanel {
      contents += btnShow
      contents += btnMark
    }

    def changeTable(t: Component) {
      contents(1) = new ScrollPane(t)
    }
  }
  contents = bp

  var marked: List[MarkedSolution] = null

  listenTo(btnMark, btnShow, btnPrev, btnNext)

  reactions += {
    case ButtonClicked(`btnMark`) => {
      marked = markSelected
      var confirm = true
      if (marked.filter({_.good}).length == 0) {
        val d = Dialog.showConfirmation(bp,
          "You have not selected any solutions. Do you want to end the run?", "End of run",
          Dialog.Options.YesNo)
        confirm = (d == Dialog.Result.Yes)
      }
      if (confirm)
        visible = false
    }

    case ButtonClicked(`btnShow`) => {
      val sd = new SolutionDialog(this, evaluated, jTable.getSelectedRows)
      sd.setLocationRelativeTo(this)
      sd.visible = true
    }

    case ButtonClicked(`btnPrev`) => {
      currentRun -= 1
      evaluated = history(currentRun)
      resetButtons
      createTable
      bp.changeTable(table)
    }

    case ButtonClicked(`btnNext`) => {
      currentRun += 1
      evaluated = history(currentRun)
      resetButtons
      createTable
      bp.changeTable(table)
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
  
  private def resetButtons() = {
    btnPrev.enabled = currentRun > 0
    btnNext.enabled = currentRun < (history.size - 1)
    histLabel.text = "(%d/%d)" format (currentRun+1, history.size)
  }

  private def createTable() = {
   jTable = new JTable(new DarwinTableModel)
   jTable.setAutoCreateRowSorter(true)
   table  = new Component {
    override lazy val peer = jTable
   }
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

class SolutionDialog(window: Window, evaluated: List[EvaluatedSolution], rows: Array[Int])
        extends Dialog(window) {

  visible = false
  modal = false

  val tp = new TabbedPane
  for (r <- rows) {
    tp.pages += new TabbedPane.Page("Solution %02d" format r,
      new SolutionPanel(evaluated(r)))
  }

  contents = tp
}

class SolutionPanel(e: EvaluatedSolution) extends GridPanel(0, 2) {
  for ((k, v) <- e.values) {
    contents += new Label(k)
    contents += new Label(v.toString)
  }
}

