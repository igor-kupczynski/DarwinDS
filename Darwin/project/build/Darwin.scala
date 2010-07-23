import java.io.File
import java.net.URL
import sbt._

class DarwinProject(info: ProjectInfo) extends DefaultProject(info)
{  
  val jBossRepo = "JBoss Repository" at "http://repository.jboss.org/maven2/"
  val scalaToolsSnaphot = "Scala Tools Snapshot" at
                          "http://www.scala-tools.org/repo-snapshots/"

  val trove = "trove" % "trove" % "2.1.1" // Used by jRS
  val ini4j = "org.ini4j" % "ini4j" % "0.5.1"
  val args4j = "args4j" % "args4j" % "2.0.9"

  val specs = "org.scala-tools.testing" % "specs_2.8.0" %
              "1.6.5" % "test" withSources()
  val scalatest = "org.scalatest" % "scalatest" %
                  "1.2" % "test" withSources()
  val scalacheck = "org.scala-tools.testing" % "scalacheck_2.8.0" %
                   "1.7" % "test"  withSources()
  val mockito = "org.mockito" % "mockito-all" % "1.8.4" % "test" withSources()
  val junit = "junit" % "junit" % "4.8.1" % "test"

  lazy val downloadJrs = task {
    val url = new URL("http://github.com/downloads/puszczyk/DarwinDS/jrs_2010-04-30.jar")
    val target = new File("lib/jrs_2010-04-30.jar")

    FileUtilities.download(url, target, log)
  }
}
