package pl.poznan.put.darwin.gui

import swing._
import event._
import pl.poznan.put.darwin.simulation._
import pl.poznan.put.darwin.model.solution._
import javax.swing.table.AbstractTableModel
import java.lang.{Class, String}
import collection.mutable.ArrayBuffer
import javax.swing.{Box, JTable}
import java.io.{FileWriter, File}
import javax.swing.filechooser.FileNameExtensionFilter

object DarwinDialog {
  def show(window: Window,
           sim: Simulation,
           history: ArrayBuffer[List[EvaluatedSolution]]): List[MarkedSolution] = {
    val d = new DarwinDialog(window, sim, history)
    d.setLocationRelativeTo(window)
    d.visible = true
    val result = d.marked
    d.close
    result
  }
}
  
class DarwinDialog(window: Window, val sim: Simulation,
                   history: ArrayBuffer[List[EvaluatedSolution]])
    extends Dialog(window) {

  var currentRun = history.size - 1
  var evaluated = history(currentRun)

  visible = false
  modal = true
  private val btnMark = new Button("Next iteration")
  private val btnShow = new Button("Solution details")
  private val btnEnd = new Button("End the algorithm")

  private val btnPrev = new Button("Previous")
  private val btnNext = new Button("Next")
  private val histLabel = new Label()
  resetButtons

  var jTable: JTable = _
  var table: Component = _
  createTable

  def tablePanel(): ScrollPane = {
     new ScrollPane(table) {
       preferredSize = new Dimension(1000, 515)
     }
  }

  val bp =  new BoxPanel(Orientation.Vertical) {
    contents += new FlowPanel {
      contents += new Label("History:")
      contents += btnPrev
      contents += histLabel
      contents += btnNext
    }
    contents += tablePanel()
    contents += new FlowPanel {
      contents += btnEnd
      contents += new Label("                 ")
      contents += btnShow
      contents += btnMark
    }

    def changeTable(t: Component) {
      val sp = new ScrollPane(t)
      sp.horizontalScrollBarPolicy = ScrollPane.BarPolicy.AsNeeded
      contents(1) = sp
    }
  }
  contents = bp

  var marked: List[MarkedSolution] = null

  listenTo(btnMark, btnShow, btnEnd, btnPrev, btnNext)

  reactions += {
    case ButtonClicked(`btnMark`) => {
      marked = markSelected
      if (marked.filter({_.good}).length == 0) {
        val d = Dialog.showMessage(bp,
          "You have not selected any solutions. Click the checkbox in 'Is good?' column to mark.",
          "No solutions are selected")
      } else {
        visible = false
      }
    }

    case ButtonClicked(`btnEnd`) => {
      val d = Dialog.showConfirmation(bp,
          "Do you want to end this run of the algorithm? Solutions marked as good will be saved.",
          "Do you want to end?", Dialog.Options.YesNo)
      var confirm = (d == Dialog.Result.Yes)
      if (confirm) {
        val chooser = new FileChooser(new File ("."))
        val filter = new FileNameExtensionFilter("Text files", "txt")
        chooser.fileFilter = filter

        val rc = chooser.showSaveDialog(bp)
        if (rc == FileChooser.Result.Approve) {
          val file = chooser.selectedFile
          saveSolutions(file)
          visible = false
        }
      }
    }

    case ButtonClicked(`btnShow`) => {
      val sel = jTable.getSelectedRows
      if (sel.size > 0) {
        val sd = new SolutionDialog(this, evaluated, sel)
        sd.setLocationRelativeTo(this)
        sd.visible = true
      }
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

  private def saveSolutions(file: File) = {
    val out = new FileWriter(file)
    try {
      val m = jTable.getModel
      var idx = -1
      evaluated.map(e => {
        idx += 1
        val good: Boolean = (m.getValueAt(idx, 1)).asInstanceOf[Boolean]
        if (good) {
          out.write("--- Solution %02d ---\n\n" format idx)
          out.write("Variables:\n")
          for ((k, v) <- e.values.toList.sortWith(
            (a, b) => {
              a._1 < b._1
            })) {
            out.write("* %s: %s\n" format (k, v.toString))
          }
          out.write("\nGoals:\n")
          for (g <- e.goals) {
            for (p <- e.sim.config.PERCENTILES) {
              out.write("* %s_%s: %s\n" format (g.name, p.toString, e.getPercentile(g, p).toString))
            }
          }
          out.write("\n\n")
        }
      })
    } finally {
      out.close
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
    jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF)
    jTable.setAutoCreateRowSorter(true)
    for (idx <- 2 to (jTable.getColumnCount - 1)) {
      val col = jTable.getColumnModel.getColumn(idx)
      col.setPreferredWidth(85)
    }
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
      val result: java.util.Vector[String] = new java.util.Vector()
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
            row.add(round(e.getPercentile(g, p)).asInstanceOf[Object])
          }
        }
        result.add(row)
        rowIdx += 1
      }
      result
    }

    private def round(x: Double): Double = {
        val num = math.pow(10, sim.config.DIGITS_AFTER_DOT)
        (num * x).round.doubleValue / num
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
  for ((k, v) <- e.values.toList.sortWith(
      (a, b) => {
        a._1 < b._1
    })) {
    contents += new Label(k)
    contents += new Label(v.toString)
  }
}

