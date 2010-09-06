package pl.poznan.put.darwin.gui

import swing._
import event._

class DarwinWindow extends BorderPanel {
  
  add(new Selectors, BorderPanel.Position.Center)
  add(new Label("South"), BorderPanel.Position.South)

}
