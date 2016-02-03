package gdx.scala.demo


import java.util.{Timer, TimerTask}

import com.badlogic.ashley.core.{Entity, ComponentMapper}
import com.badlogic.ashley.utils.ImmutableArray
import gdx.scala.demo.components.Bullet
import scala.collection.JavaConversions._
import com.badlogic.gdx.utils.Array
/**
  * Created by rtorres on 2/2/2016.
  */
trait Trigger {
  def pullTrigger() : Unit
}
case class ShipTrigger(entities : ImmutableArray[Entity], componentMapper: ComponentMapper[Bullet]) extends Trigger {
  val bullets = new ImmutableArray[Bullet](new Array(entities.map(componentMapper.get).toArray))
  var canPull = true
  startTimer()

  def startTimer() : TimerTask = {
    val timer = new Timer
    val allowTrigger = new java.util.TimerTask {
      override def run() : Unit = {
        canPull = true
        println("Can shoot!")
      }
    }
    timer.schedule(allowTrigger, 500L, 500L)
    allowTrigger
  }

  override def pullTrigger(): Unit = {
    val nextBullet = bullets find(_.inFlight == false)
    if(nextBullet.isDefined && canPull) {
      nextBullet.get.triggered = true
    }
    canPull = false
  }
}
