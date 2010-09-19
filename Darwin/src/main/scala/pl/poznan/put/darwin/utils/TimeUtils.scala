package pl.poznan.put.darwin.utils;

object TimeUtils {

  def time[A](name: String, f: => A): A = {
        val t1 = System.nanoTime
        val r: A = f
        val t2 = System.nanoTime
        System.err.println("Routine " + name + " took: " + (t2 - t1).asInstanceOf[Float] + " nanosecs")
        r
    }
}