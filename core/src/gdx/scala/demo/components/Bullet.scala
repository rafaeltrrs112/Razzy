package gdx.scala.demo.components

import com.badlogic.ashley.core.Component


/**
  * Bullet component object.
  */
class Bullet extends Component {
  var inFlight: Boolean = false
  var triggered: Boolean = false
  var originalPosition: Option[Point] = None
}

object BulletConst {
  val Speed: Int = 5
  val Tag: String = "bullet"
}

case class Point(x: Float, y: Float) {
  def dst(v: Point): Float = {
    val x_d: Float = v.x - x
    val y_d: Float = v.y - y
    Math.sqrt(x_d * x_d + y_d * y_d).toFloat
  }

  /**
    * Measures distance.
    *
    * @param x The x-component of the other vector
    * @param y The y-component of the other vector
    * @return the distance between this and the other vector */
  def dst(x: Float, y: Float): Float = {
    val x_d: Float = x - this.x
    val y_d: Float = y - this.y
    Math.sqrt(x_d * x_d + y_d * y_d).toFloat
  }
}


