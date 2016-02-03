package gdx.scala.demo.system

import com.badlogic.ashley.core.{ComponentMapper, Entity, Family, Engine}
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Vector2
import com.uwsoft.editor.renderer.components.{DimensionsComponent, TransformComponent}
import com.uwsoft.editor.renderer.utils.ComponentRetriever
import gdx.scala.demo.Player
import gdx.scala.demo.components.{Bullet, Point, Collidable}
import scala.collection.JavaConversions._

/**
  *
  */
class CollisionSystem(player: Player, engine: Engine) extends IteratingSystem(Family.all(classOf[Collidable]).get) {
  val collidableMapper = ComponentMapper.getFor(classOf[Collidable])

  var collidableEntities: ImmutableArray[Entity] = engine.getEntitiesFor(Family.all(classOf[Collidable]).get)
  var bulletEntities: ImmutableArray[Entity] = engine.getEntitiesFor(Family.all(classOf[Bullet]).get)

  collidableEntities.foreach(setOriginalPosition)

  override def processEntity(entity: Entity, deltaTime: Float): Unit = {
    val collidableEntity = collidableMapper.get(entity)
    bulletCollisionCheck(collidableEntity)
  }

  //TODO : Update collision component position with the transform component's position
  //TODO : Test collision system
  //TODO : Create collidable trait to handle destruction of entities at collision
  //TODO   i.e => if (collided) collidable.collideEvent(collidedWith : Collidable) = ??? etc...
  def bulletCollisionCheck(collidable: Collidable): Boolean = {
    val collidedEntities : Iterable[Entity] = bulletEntities.filter(collidesWith(collidable))
    if(collidedEntities.nonEmpty){
      println("Collided!")
    } else {
      println("Not collided!")
    }
    false
  }

  def collidesWith(collidable: Collidable) : Entity => Boolean = entity => {
    val bullet = ComponentRetriever.get(entity, classOf[Bullet])
    val bulletDimensions = ComponentRetriever.get(entity, classOf[DimensionsComponent])

    val width = bulletDimensions.width
    val height = bulletDimensions.height

    collidable.position.get.dst(bullet.originalPosition.get) <= ((width / 2) + (collidable.width.get / 2))
  }


  def setOriginalPosition(bulletEntity: Entity): Unit = {
    val collidable = collidableMapper.get(bulletEntity)
    val transformComponent = ComponentRetriever.get(bulletEntity, classOf[TransformComponent])
    val dimensionComponent = ComponentRetriever.get(bulletEntity, classOf[DimensionsComponent])

    collidable.position = Some(Point(transformComponent.x, transformComponent.y))
    collidable.width = Some(dimensionComponent.width)
  }
}
