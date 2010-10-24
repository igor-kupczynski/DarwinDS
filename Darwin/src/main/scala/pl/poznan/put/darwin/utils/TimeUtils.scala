package pl.poznan.put.darwin.utils

import com.weiglewilczek.slf4s.Logger;

object TimeUtils {

  val logger: Logger = Logger(TimeUtils.getClass)

  def time[A](name: String, f: => A): A = {
        val t1 = System.nanoTime
        val r: A = f
        val t2 = System.nanoTime
        logger info "Routine %s took: %f nanosecs".format(name, (t2 - t1).asInstanceOf[Float])
        r
    }
}