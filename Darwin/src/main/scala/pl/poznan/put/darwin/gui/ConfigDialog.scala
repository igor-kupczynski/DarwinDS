package pl.poznan.put.darwin.gui

import swing.{Window, Dialog}
import pl.poznan.put.darwin.model.Config

object ConfigDialog {
  def show(window: Window, default: Config): Config = {
    val d = new ConfigDialog(window, default)
    d.setLocationRelativeTo(window)
    d.visible = true
    d.config
  }
}

class ConfigDialog(window: Window, default: Config)
    extends Dialog(window) {

    var config: Config = _
  
  
}