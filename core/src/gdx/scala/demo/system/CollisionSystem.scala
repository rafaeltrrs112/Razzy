package gdx.scala.demo.system

import com.badlogic.ashley.core.{ComponentMapper, Entity, Family, Engine}
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.uwsoft.editor.renderer.components.{DimensionsComponent, TransformComponent}
import com.uwsoft.editor.renderer.utils.ComponentRetriever
import gdx.scala.demo.character.Player
import gdx.scala.demo.components.{Bullet, Point, Collidable}
import scala.collection.JavaConversions._

object CollisionSystem {
  def apply(engine: Engine, player: Player): CollisionSystem = new CollisionSystem(engine, player)
}

class CollisionSystem(engine: Engine, player: Player) extends IteratingSystem(Family.all(classOf[Collidable]).get) {
  val collidableMapper = ComponentMapper.getFor(classOf[Collidable])
  val bulletMapper: ComponentMapper[Bullet] = ComponentMapper.getFor(classOf[Bullet])
  val transformMapper : ComponentMapper[TransformComponent] = ComponentMapper.getFor(classOf[TransformComponent])
  val dimensionMapper : ComponentMapper[DimensionsComponent] = ComponentMapper.getFor(classOf[DimensionsComponent])
  var collidableEntities = engine.getEntitiesFor(Family.all(classOf[Collidable]).get)
  var bulletEntities: ImmutableArray[Entity] = engine.getEntitiesFor(Family.all(classOf[Bullet]).get)
  collidableEntities.foreach(setOriginalPosition)
  println(collidableEntities.foreach(_.getComponents.foreach(println)))
  var collisions = 0

  override def processEntity(entity: Entity, deltaTime: Float): Unit = {
    val transformComponent = transformMapper.get(entity)
    val collidableEntity = collidableMapper.get(entity)
    updatePosition(collidableEntity, transformMapper.get(entity), dimensionMapper.get(entity))
    bulletCollisionCheck(collidableEntity)
  }

  def nonBulletCollidables : Iterable[Entity] = {
    engine.getEntitiesFor(Family.all(classOf[Collidable]).get).filterNot(isBullet)
  }

  def isBullet(entity: Entity) : Boolean = entity.getComponent(classOf[Bullet]) != null

  //TODO : Update collision component position with the transform component's position
  //TODO : Test collision system
  //TODO : Create collidable trait to handle destruction of entities at collision
  //TODO   i.e => if (collided) collidable.collideEvent(collidedWith : Collidable) = ??? etc...
  def bulletCollisionCheck(collidable: Collidable): Boolean = {
    val collidedEntities: Iterable[Entity] = bulletEntities.filter(collidesWith(collidable))
    if (collidedEntities.nonEmpty) {
      println("Collided!")
    } else {
    }
    false
  }

  def collidesWith(collidable: Collidable): Entity => Boolean = entity => {
    val bulletDimensions = ComponentRetriever.get(entity, classOf[DimensionsComponent])
    val bulletTransformComp = transformMapper.get(entity)
    val bulletPosition = Point(bulletTransformComp.x + bulletDimensions.width / 2, bulletTransformComp.y + bulletDimensions.height / 2)

    val bulletHeight = bulletDimensions.height
    val bulletWidth = bulletDimensions.width

    val result = collidable.position.get.dst(bulletPosition) <= ((bulletHeight / 2) + (collidable.height.get / 2))
    val result2 = collidable.position.get.dst(bulletPosition) <= ((bulletWidth / 2) + (collidable.width.get / 2))

    result || result2
  }

  def setOriginalPosition(collidableEntity: Entity): Unit = {
    val collidable = collidableMapper.get(collidableEntity)
    val transformComponent = ComponentRetriever.get(collidableEntity, classOf[TransformComponent])
    val dimensionComponent = ComponentRetriever.get(collidableEntity, classOf[DimensionsComponent])

    collidable.position = Some(Point(transformComponent.x, transformComponent.y))
    collidable.width = Some(dimensionComponent.width)
    collidable.height = Some(dimensionComponent.height)
  }

  def updatePosition(collidable : Collidable, transformComponent: TransformComponent, dimensionsComponent: DimensionsComponent) : Unit  = {
    collidable.position = Some(Point(transformComponent.x + dimensionsComponent.width / 2, transformComponent.y + dimensionsComponent.height / 2))
  }
}
