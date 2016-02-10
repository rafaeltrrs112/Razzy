package gdx.scala.demo.system

import com.badlogic.ashley.core._
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.uwsoft.editor.renderer.components.{DimensionsComponent, TransformComponent}
import com.uwsoft.editor.renderer.utils.ComponentRetriever
import gdx.scala.demo.Window
import gdx.scala.demo.character.{Player, ShipTrigger}
import gdx.scala.demo.components._

import scala.collection.JavaConversions._


class BulletSystem(engine: Engine, player: Player) extends IteratingSystem(Family.all(classOf[Bullet]).get) {
  val playerBullets: ImmutableArray[Entity] = engine.getEntitiesFor(Family.all(classOf[PlayerBullet]).get)

  val enemyBullets: ImmutableArray[Entity] = engine.getEntitiesFor(Family.all(classOf[EnemyBullet]).get)
  val playerBulletMapper: ComponentMapper[PlayerBullet] = ComponentMapper.getFor(classOf[PlayerBullet])
  val enemyBulletMapper: ComponentMapper[EnemyBullet] = ComponentMapper.getFor(classOf[EnemyBullet])
  val allBullets = playerBullets ++ enemyBullets

  player.trigger = Some(ShipTrigger(playerBullets, playerBulletMapper))

  playerBullets.foreach(setOriginalPosition)

  override def processEntity(entity: Entity, deltaTime: Float): Unit = {
    bulletComponent(entity) match {
      case Some(playerBullet: PlayerBullet) => println("Is a player bullet.")
        processPlayerBullet(entity, deltaTime)
      case
        Some(enemyBullet: EnemyBullet) => println("Is an enemy bullet")
    }
  }


  def processPlayerBullet(entity: Entity, deltaTime: Float): Unit = {
    val bullet: PlayerBullet = playerBulletMapper.get(entity)
    val transformComponent: TransformComponent = ComponentRetriever.get(entity, classOf[TransformComponent])
    val dimensionComponent: DimensionsComponent = ComponentRetriever.get(entity, classOf[DimensionsComponent])

    bullet.inFlight match {
      case true => updateBullet(bullet, transformComponent, deltaTime)
      case false => if (bullet.triggered) shootBullet(bullet, transformComponent, dimensionComponent)
    }
  }

  def updateBullet(bullet: PlayerBullet, transformComponent: TransformComponent, delta: Float): Unit = {
    inView(transformComponent) match {
      case true => transformComponent.y += (PlayerBullet.Speed * delta)
      case false => reInitBullet(bullet, transformComponent)
    }
  }


  def inView(transformComponent: TransformComponent): Boolean = transformComponent.y <= Window.Height

  def reInitBullet(bullet: PlayerBullet, transformComponent: TransformComponent): Unit = {
    transformComponent.y = bullet.originalPosition.get.y
    transformComponent.x = bullet.originalPosition.get.x
    bullet.inFlight = false
    bullet.triggered = false
  }

  def shootBullet(bullet: PlayerBullet, transformComponent: TransformComponent, dimensionsComponent: DimensionsComponent) = {
    transformComponent.x = player.x + player.width / 2
    transformComponent.y = player.y + player.height / 2
    bullet.inFlight = true
  }

  def setOriginalPosition(bulletEntity: Entity): Unit = {
    val bulletComponent: PlayerBullet = playerBulletMapper.get(bulletEntity)
    val transformComponent: TransformComponent = ComponentRetriever.get(bulletEntity, classOf[TransformComponent])
    bulletComponent.originalPosition = Some(Point(transformComponent.x, transformComponent.y))
  }

  def bulletComponent(entity: Entity): Option[Bullet] = {
    println(playerBulletMapper.get(entity))
    List(Some(playerBulletMapper.get(entity)), Some(enemyBulletMapper.get(entity)))
      .find(_.isDefined)
      .get
  }
}


object BulletSystem {
  def apply(engine: Engine, player: Player): BulletSystem = new BulletSystem(engine, player)
}
