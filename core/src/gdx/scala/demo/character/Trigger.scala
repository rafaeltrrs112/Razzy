package gdx.scala.demo.character

import java.util.{Timer, TimerTask}

import com.badlogic.ashley.core.{ComponentMapper, Entity}
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.utils.Array
import gdx.scala.demo.components.Bullet

import scala.collection.JavaConversions._
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
      }
    }
    timer.schedule(allowTrigger, 200L, 200L)
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
