package pl.poznan.put.darwin

import scala.swing._
import gui.DarwinWindow
import com.weiglewilczek.slf4s.Logging

object RunnerGUI extends SimpleSwingApplication with Logging {

  def top = new MainFrame {
    title = "DARWIN"
    try {
      contents = new DarwinWindow(this)
    } catch {
      case e => {
        logger.error("An error occured", e)
        System.exit(-1)
      }
    }
  }

}
