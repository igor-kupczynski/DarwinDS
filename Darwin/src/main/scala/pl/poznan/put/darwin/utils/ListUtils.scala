package pl.poznan.put.darwin.utils

object ListUtils {

  def shuffle[T](xs: List[T]): List[T] = xs match {
    case List() => List()
    case xs => { 
      val i = RNG.get().nextInt(xs.size);
      xs(i) :: shuffle(xs.take(i) ++ xs.drop(i+1))
    }
  }
  
}
