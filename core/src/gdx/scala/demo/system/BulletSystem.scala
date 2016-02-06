package gdx.scala.demo.system


import com.badlogic.ashley.core._
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.uwsoft.editor.renderer.components.{DimensionsComponent, TransformComponent}
import com.uwsoft.editor.renderer.utils.ComponentRetriever
import gdx.scala.demo.character.{ShipTrigger, Player}
import gdx.scala.demo.components.{Point, Bullet}
import scala.collection.JavaConversions._


class BulletSystem(engine: Engine, player: Player) extends IteratingSystem(Family.all(classOf[Bullet]).get()) {
  val entities : ImmutableArray[Entity] = engine.getEntitiesFor(Family.all(classOf[Bullet]).get())
  val bulletMapper : ComponentMapper[Bullet] = ComponentMapper.getFor(classOf[Bullet])

  player.trigger = Some(ShipTrigger(entities, bulletMapper))

  entities.foreach(setOriginalPosition)

  override def processEntity(entity: Entity, deltaTime: Float): Unit = {
    val bullet : Bullet = bulletMapper.get(entity)
    val transformComponent : TransformComponent = ComponentRetriever.get(entity, classOf[TransformComponent])
    val dimensionComponent : DimensionsComponent = ComponentRetriever.get(entity, classOf[DimensionsComponent])

    bullet.inFlight match {
      case true => updateBullet(bullet, transformComponent, deltaTime)
      case false => if (bullet.triggered) shootBullet(bullet, transformComponent, dimensionComponent)
    }
  }

  def updateBullet(bullet: Bullet, transformComponent: TransformComponent, delta: Float): Unit = {
    inView(transformComponent) match {
      case true => transformComponent.y += (Bullet.Speed * delta)
      case false => reInitBullet(bullet, transformComponent)
    }
  }

  def inView(transformComponent: TransformComponent): Boolean = transformComponent.y <= 5

  def reInitBullet(bullet: Bullet, transformComponent: TransformComponent): Unit = {
    transformComponent.y = bullet.originalPosition.get.y
    transformComponent.x = bullet.originalPosition.get.x
    bullet.inFlight = false
    bullet.triggered = false
  }

  def shootBullet(bullet: Bullet, transformComponent: TransformComponent, dimensionsComponent: DimensionsComponent) = {
    transformComponent.x = player.x + player.width / 2
    transformComponent.y = player.y + player.height / 2
    bullet.inFlight = true
  }

  def setOriginalPosition(bulletEntity: Entity): Unit = {
    val bulletComponent : Bullet = bulletMapper.get(bulletEntity)
    val transformComponent : TransformComponent = ComponentRetriever.get(bulletEntity, classOf[TransformComponent])
    bulletComponent.originalPosition = Some(Point(transformComponent.x, transformComponent.y))
  }
}

object BulletSystem {
  def apply(engine: Engine, player: Player): BulletSystem = new BulletSystem(engine, player)
}
