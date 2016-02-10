package gdx.scala.demo.system

import com.badlogic.ashley.core.{ComponentMapper, Engine, Entity, Family}
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.uwsoft.editor.renderer.components.{DimensionsComponent, TransformComponent}
import com.uwsoft.editor.renderer.utils.ComponentRetriever
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
}

object EnemyOffenseSystem {
  def apply(interval: Float): EnemyOffenseSystem = new EnemyOffenseSystem(interval)
}

class EnemyOffenseSystem(interval: Float) extends IntervalSystem(interval) {
  private var enemyEntities: Option[ImmutableArray[Entity]] = None
  private var enemyBulletEntities: Option[ImmutableArray[Entity]] = None

  override def updateInterval(): Unit = {
    val randIndex = Random.nextInt(enemyEntities.get.size)

    /*
     * Get a random enemy to shoot the bullet.
     * Get the next available bullet that the enemy can shoot.
     */
    val randomEnemy = enemyEntities.get.get(randIndex)
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
      case None => println("No bullet's available to shoot.")
    }

    /*
     * Update all bullets that are in-flight. This should update in interval fashion.
     * This system will need to inter-op with a system that can do fluid updates.
     */
    enemyBulletEntities.get.foreach(updateBullet)

  }

  def playBullet(enemyEntity: Entity, bulletEntity: Entity): Unit = {
    val bulletTransform: TransformComponent = Retriever.MainRetriever.transformMapper.get(bulletEntity)

    if (inView(bulletTransform)) println("In view")
    /*updateBullet(bulletEntity)*/
  }

  override def addedToEngine(engine: Engine): Unit = {
    enemyEntities = Some(engine.getEntitiesFor(Family.all(classOf[PeonComponent]).get()))
    enemyBulletEntities = Some(engine.getEntitiesFor(Family.all(classOf[EnemyBullet]).get()))
    enemyBulletEntities.get.foreach(setOriginalPosition)
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
    val availableBullet: Option[Entity] = enemyBulletEntities.get.find(notInFlight)
    availableBullet
  }

  def notInFlight(entity: Entity): Boolean = !Retriever.EnemyBulletMapper.get(entity).inFlight

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
