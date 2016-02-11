package gdx.scala.demo.system

import java.util.{Timer, TimerTask}

import com.badlogic.ashley.core.{ComponentMapper, Engine, Entity, Family}
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g2d.Animation
import com.uwsoft.editor.renderer.components.sprite.{SpriteAnimationComponent, SpriteAnimationStateComponent}
import com.uwsoft.editor.renderer.components.{DimensionsComponent, TransformComponent}
import com.uwsoft.editor.renderer.utils.ComponentRetriever
import gdx.scala.demo.character.Player
import gdx.scala.demo.components._

import scala.collection.JavaConversions._
import scala.util.Random

/**
  * System responsible for handling the enemy's attack pattern.
  */
case class DefaultRetriever(transformMapper: ComponentMapper[TransformComponent], dimensionsMapper: ComponentMapper[DimensionsComponent])

object Retriever {
  val MainRetriever: DefaultRetriever = DefaultRetriever(ComponentMapper.getFor(classOf[TransformComponent]), ComponentMapper.getFor(classOf[DimensionsComponent]))
  val EnemyBulletMapper: ComponentMapper[EnemyBullet] = ComponentMapper.getFor(classOf[EnemyBullet])
  val SpriteAnimationMapper: ComponentMapper[SpriteAnimationComponent] = ComponentMapper.getFor(classOf[SpriteAnimationComponent])
  val SpriteAnimationStateMapper: ComponentMapper[SpriteAnimationStateComponent] = ComponentMapper.getFor(classOf[SpriteAnimationStateComponent])
}

object EnemyOffenseSystem {
  def apply(engine: Engine, player: Player): EnemyOffenseSystem = new EnemyOffenseSystem(engine, player)
}

class EnemyOffenseSystem(engine: Engine, player: Player) extends IteratingSystem(Family.all(classOf[EnemyBullet]).get) {
  private val enemyEntities: ImmutableArray[Entity] = engine.getEntitiesFor(Family.all(classOf[PeonComponent]).get)
  private val enemyBulletEntities: ImmutableArray[Entity] = engine.getEntitiesFor(Family.all(classOf[EnemyBullet]).get)
  private val shields: ImmutableArray[Entity] = engine.getEntitiesFor(Family.all(classOf[Shield]).get)

  enemyBulletEntities.foreach(setOriginalPosition)

  private def startTimer(): TimerTask = {
    val timer = new Timer
    val allowTrigger = new java.util.TimerTask {
      override def run(): Unit = {
        println(enemyEntities.size())
        val randIndex = Random.nextInt(enemyEntities.size)

        /*
         * Get a random enemy to shoot the bullet.
         * Get the next available bullet that the enemy can shoot.
         */
        val randomEnemy = enemyEntities.get(randIndex)
        val chosenBullet = nextBullet

        /*
         * If a bullet is available then trigger it and place it in
         * front of the enemy that is shooting it.
         */
        chosenBullet match {
          case Some(_) => {
            val notInFlight = !Retriever.EnemyBulletMapper.get(chosenBullet.get).inFlight
            if (notInFlight) triggerBullet(randomEnemy, chosenBullet.get)
          }
          case None =>
        }
      }
    }
    timer.schedule(allowTrigger, 500l, 100l)
    allowTrigger
  }

  startTimer()


  override def processEntity(entity: Entity, deltaTime: Float): Unit = {
    updateBullet(entity, deltaTime)
    shields.foreach(destroyShields(_, entity))
  }


  def destroyShields(shieldEntity: Entity, bulletEntity: Entity): Unit = {
    val bulletTransform = Retriever.MainRetriever.transformMapper.get(bulletEntity)
    val bulletDimension = Retriever.MainRetriever.dimensionsMapper.get(bulletEntity)

    val shieldTransform = Retriever.MainRetriever.transformMapper.get(shieldEntity)
    val shieldDimension = Retriever.MainRetriever.dimensionsMapper.get(shieldEntity)

    val totalWidth = shieldDimension.width / 2 + bulletDimension.width / 2
    val totalHeight = shieldDimension.height / 2 + bulletDimension.height / 2

    val pointDistance = Point(bulletTransform.x, bulletTransform.y).dst(Point(shieldTransform.x, shieldTransform.y))
    val colliding = pointDistance <= totalWidth || pointDistance <= totalHeight

    if (colliding) {
      engine.removeEntity(shieldEntity)
      reInitBullet(Retriever.EnemyBulletMapper.get(bulletEntity), bulletTransform)
      //      engine.removeEntity(bulletEntity)
    }
  }


  def playBullet(enemyEntity: Entity, bulletEntity: Entity): Unit = {
    val bulletTransform: TransformComponent = Retriever.MainRetriever.transformMapper.get(bulletEntity)

    //    if (inView(bulletTransform)) println("In view")
    /*updateBullet(bulletEntity)*/
  }

  def triggerBullet(enemyEntity: Entity, bulletEntity: Entity): Unit = {
    val enemyTransform: TransformComponent = Retriever.MainRetriever.transformMapper.get(enemyEntity)
    val enemyDimension: DimensionsComponent = Retriever.MainRetriever.dimensionsMapper.get(enemyEntity)

    val bulletComponent: Bullet = Retriever.EnemyBulletMapper.get(bulletEntity)
    val bulletTransform: TransformComponent = Retriever.MainRetriever.transformMapper.get(bulletEntity)

    bulletComponent.triggered = true
    shootBullet(enemyTransform, enemyDimension, bulletComponent, bulletTransform)
  }


  def nextBullet: Option[Entity] = {
    val availableBullet: Option[Entity] = enemyBulletEntities.find(notInFlight)
    availableBullet
  }

  def notInFlight(entity: Entity): Boolean = !Retriever.EnemyBulletMapper.get(entity).inFlight

  def updateBullet(entity: Entity, deltaTime: Float): Unit = {
    val transformComponent = Retriever.MainRetriever.transformMapper.get(entity)
    val bulletComponent = Retriever.EnemyBulletMapper.get(entity)

    inView(transformComponent) match {
      case true =>
        transformComponent.y -= (PlayerBullet.Speed * deltaTime)
        if (collidesWithPlayer(player, entity)) explosionState(player.player)
      case false => reInitBullet(bulletComponent, transformComponent)
    }
  }

  def explosionState(entity: Entity): Unit = {
    val animationComponent = Retriever.SpriteAnimationMapper.get(entity)
    val animationStateComponent = Retriever.SpriteAnimationStateMapper.get(entity)
    animationStateComponent.set(animationComponent.frameRangeMap.get("dead"), 0, Animation.PlayMode.LOOP)
  }

  def collidesWithPlayer(player: Player, entity: Entity): Boolean = {
    val bulletDimensions = ComponentRetriever.get(entity, classOf[DimensionsComponent])
    val bulletTransformComp = Retriever.MainRetriever.transformMapper.get(entity)
    val bulletPosition = Point(bulletTransformComp.x + bulletDimensions.width / 2, bulletTransformComp.y + bulletDimensions.height / 2)

    val bulletHeight = bulletDimensions.height
    val bulletWidth = bulletDimensions.width

    val result = player.currentPosition.dst(bulletPosition) <= ((bulletHeight / 2) + (player.height / 2))
    val result2 = player.currentPosition.dst(bulletPosition) <= ((bulletWidth / 2) + (player.width / 2))

    result || result2
  }

  def inFlightBullet(entity: Entity): Boolean = Retriever.EnemyBulletMapper.get(entity).inFlight

  def inView(transformComponent: TransformComponent): Boolean = transformComponent.y >= 0


  def reInitBullet(bullet: Bullet, transformComponent: TransformComponent): Unit = {
    transformComponent.y = bullet.originalPosition.get.y
    transformComponent.x = bullet.originalPosition.get.x
    bullet.inFlight = false
    bullet.triggered = false
  }

  def setOriginalPosition(bulletEntity: Entity): Unit = {
    val bulletComponent: Bullet = Retriever.EnemyBulletMapper.get(bulletEntity)
    val transformComponent: TransformComponent = ComponentRetriever.get(bulletEntity, classOf[TransformComponent])
    bulletComponent.originalPosition = Some(Point(transformComponent.x, transformComponent.y))
  }

  def shootBullet(enemyTransform: TransformComponent, enemyDimension: DimensionsComponent, bullet: Bullet, bulletTransform: TransformComponent) = {
    bulletTransform.x = enemyTransform.x - enemyDimension.width / 2
    bulletTransform.y = enemyTransform.y - enemyDimension.height / 2
    bullet.inFlight = true
  }

}

object EnemyBulletUpdater {
  def update(entity: Entity): Unit = {
    updateBullet(entity)
  }

  def updateBullet(entity: Entity): Unit = {
    val transformComponent = Retriever.MainRetriever.transformMapper.get(entity)
    val bulletComponent = Retriever.EnemyBulletMapper.get(entity)

    inView(transformComponent) match {
      case true => transformComponent.y -= 1
      case false => reInitBullet(bulletComponent, transformComponent)
    }
  }

  def inFlightBullet(entity: Entity): Boolean = Retriever.EnemyBulletMapper.get(entity).inFlight

  def inView(transformComponent: TransformComponent): Boolean = transformComponent.y >= 0

  def reInitBullet(bullet: Bullet, transformComponent: TransformComponent): Unit = {
    transformComponent.y = bullet.originalPosition.get.y
    transformComponent.x = bullet.originalPosition.get.x
    bullet.inFlight = false
    bullet.triggered = false
  }
}
