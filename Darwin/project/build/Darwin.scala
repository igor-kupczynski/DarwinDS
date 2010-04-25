import sbt._

class DarwinProject(info: ProjectInfo) extends DefaultProject(info)
{
  val specs = "org.scala-tools.testing" % "specs" % "1.6.2.1" % "test"
  val scalatest = "org.scalatest" % "scalatest" % "1.0" % "test"
  val scalacheck = "org.scala-tools.testing" % "scalacheck" % "1.6" % "test" 

}