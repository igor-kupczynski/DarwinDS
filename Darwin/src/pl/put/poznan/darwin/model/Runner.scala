package pl.put.poznan.darwin.model

object Runner {
  def main(args: Array[String]) {
    val p = DefaultProblemFactory.getProblem();
    println(p);
  }
}