import java.time.LocalDate
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter


val S1 = '\u25A1'
val S2 = '\u25A0'
val DaysInWeek = DayOfWeek.values.size


println("Hi there")


class Point(val d: Int, val w: Int) {

  require(d < DaysInWeek)

  def canEqual(a: Any) = a.isInstanceOf[Point]

  override def equals(that: Any): Boolean = {
    that match {
      case that: Point => that.canEqual(this) && this.d == that.d && this.w == that.w
      case _ => false
    }
  }

  override def hashCode: Int = d + DaysInWeek * w  // Correct only for our application

  override def toString: String = "(" + this.d + ", " + this.w + ")"
}


class Letter { // Sign? Grid?
  val height = DaysInWeek
  val width = 0
  val points = Set[Point]()

  override def toString(): String = {
    var repr = ""
    for(i <- 0 to this.height - 1) {
      for(j <- 0 to this.width - 1) {
        if (this.points.contains(new Point(i, j)))
          repr += S2
        else
          repr += S1
        repr += " "
      }
      repr += "\n"
    }
    repr
  }

  def tuplesToPoints(set: Set[(Int, Int)]) = for {x <- set} yield new Point(x._1, x._2)
}


object H extends Letter {
  override val width = 3
  override val points = tuplesToPoints(Set((1, 0), (2, 0), (3, 0), (4, 0), (5, 0), // More readable tuples for typing
                                           (3, 1),
                                           (1, 2), (2, 2), (3, 2), (4, 2), (5, 2)))
}


object E extends Letter {
  override val width = 2
  override val points = tuplesToPoints(Set((1, 0), (2, 0), (3, 0), (4, 0), (5, 0),
                                           (1, 1), (3, 1), (5, 1)))
}


object L extends Letter {
  override val width = 2
  override val points = tuplesToPoints(Set((1, 0), (2, 0), (3, 0), (4, 0), (5, 0),
                                           (5, 1)))
}


object O extends Letter {
  override val width = 3
  override val points = tuplesToPoints(Set((1, 0), (2, 0), (3, 0), (4, 0), (5, 0),
                                           (1, 1), (5, 1),
                                           (1, 2), (2, 2), (3, 2), (4, 2), (5, 2)))
}


object W extends Letter {
  override val width = 5
  override val points = tuplesToPoints(Set((1, 0), (2, 0), (3, 0), (4, 0),
                                           (4, 1), (5, 1),
                                           (3, 2), (4, 2),
                                           (4, 3), (5, 3),
                                           (1, 4), (2, 4), (3, 4), (4, 4)))
}


object R extends Letter {
  override val width = 3
  override val points = tuplesToPoints(Set((1, 0), (2, 0), (3, 0), (4, 0), (5, 0),
                                           (1, 1), (4, 1),
                                           (2, 2), (3, 2), (5, 2)))
}


object D extends Letter {
  override val width = 3
  override val points = tuplesToPoints(Set((1, 0), (2, 0), (3, 0), (4, 0), (5, 0),
                                           (1, 1), (5, 1),
                                           (2, 2), (3, 2), (4, 2)))
}


object Space extends Letter {
  override val width = 0
  override val points = Set()
}


class NiceSign(val letters: List[Letter]) extends Letter {

  override val width = getWidth(letters)
  override val points = getPoints(letters)

  private def getWidth(letters: List[Letter]): Int = {
    if (letters.isEmpty) -1 else letters.head.width + getWidth(letters.tail) + 1
  }

  private def getPoints(letters: List[Letter]): Set[Point] = {
    def loop(points: Set[Point], letters: List[Letter], shift: Int): Set[Point] = {
      if (letters.isEmpty)
        points    
      else
        loop(points union letters.head.points.map(p => new Point(p.d, p.w + shift)), letters.tail, shift + letters.head.width + 1)
    }

    loop(Set[Point](), letters, 0)
  }

  def getDates(startDate: LocalDate): Set[String] = {
    require(startDate.getDayOfWeek() == DayOfWeek.SUNDAY, "☀☀☀ Start date should be SUNDAY ☀☀☀")
    for {p <- this.points} yield startDate.plusDays(p.hashCode).toString
  }
}


def writeToFile(dates: Set[String], fileName: String) = {
  val file = new File(fileName)
  val writer = new BufferedWriter(new FileWriter(file))

  for (d <- dates.toList.sorted) {
    writer.write(d + "\n")
  }
  writer.close()
}


val helloWorld = new NiceSign(List(H, E, L, L, O, Space, W, O, R, L, D))
println(helloWorld)
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
val startDate = if (args.size > 0) LocalDate.parse(args(0), formatter) else LocalDate.now()  // maybe default to next Sunday?
val dates = helloWorld.getDates(startDate)
writeToFile(dates, "dates.txt")
