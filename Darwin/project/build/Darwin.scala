import java.io.{File, FileInputStream, BufferedInputStream, BufferedOutputStream}
import java.net.URL
import sbt._

class DarwinProject(info: ProjectInfo) extends DefaultProject(info)
{
  val jBossRepo = "JBoss Repository" at "http://repository.jboss.org/maven2/"

  val trove = "trove" % "trove" % "2.1.1" // Used by jRS

  val specs = "org.scala-tools.testing" % "specs" % "1.6.2.1" % "test"
  val scalatest = "org.scalatest" % "scalatest" % "1.0" % "test"
  val scalacheck = "org.scala-tools.testing" % "scalacheck" % "1.6" % "test" 
  val mockito = "org.mockito" % "mockito-all" % "1.8.1" % "test"
  val junit = "junit" % "junit" % "4.8.1" % "test"

  lazy val downloadJrs = task {
    val url = new URL("http://github.com/downloads/puszczyk/DarwinDS/jrs-0.0.1-SNAPSHOT.jar")
    val target = new File("lib/jrs-0.0.1-SNAPSHOT.jar")

    FileUtilities.download(url, target, log)
  }
}