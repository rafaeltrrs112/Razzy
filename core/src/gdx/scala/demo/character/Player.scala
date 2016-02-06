package gdx.scala.demo.character

import java.awt.Point

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.{Gdx, Input}
import com.uwsoft.editor.renderer.components.sprite.{SpriteAnimationComponent, SpriteAnimationStateComponent}
import com.uwsoft.editor.renderer.components.{DimensionsComponent, TransformComponent}
import com.uwsoft.editor.renderer.scripts.IScript
import com.uwsoft.editor.renderer.utils.ComponentRetriever

/**
  * Created by rtorres on 2/2/2016.
  */
class Player(world: World, var trigger: Option[Trigger] = None) extends IScript {
  val speed = new Point(5, 0)
  var player: Entity = null
  var transformComponent: TransformComponent = null
  var dimensionsComponent: DimensionsComponent = null
  var spriteAnimationComponent: SpriteAnimationComponent = null
  var spriteAnimationStateComponent: SpriteAnimationStateComponent = null
  var bulletTransformComponent: TransformComponent = null

  override def init(entity: Entity): Unit = {
    player = entity
    transformComponent = ComponentRetriever.get(entity, classOf[TransformComponent])
    dimensionsComponent = ComponentRetriever.get(entity, classOf[DimensionsComponent])

  }

  override def dispose(): Unit = {

  }


  override def act(delta: Float): Unit = {
    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      transformComponent.x = transformComponent.x - (speed.x * delta)
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      transformComponent.x = transformComponent.x + speed.x * delta
    }
    if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && trigger.isDefined) {
      trigger.get.pullTrigger()
    }

  }

  def x = transformComponent.x

  def y = transformComponent.y

  def width = dimensionsComponent.width

  def height = dimensionsComponent.height
}
