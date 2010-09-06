package pl.poznan.put.darwin

import scala.swing._
import event._
import gui.DarwinWindow

object RunnerGUI extends SimpleSwingApplication {

  def top = new MainFrame {
    title = "DARWIN"
    contents = new DarwinWindow()
  }

}
