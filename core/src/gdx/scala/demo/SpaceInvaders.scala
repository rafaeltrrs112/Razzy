package gdx.scala.demo

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.{ApplicationAdapter, Gdx}
import com.uwsoft.editor.renderer.SceneLoader
import com.uwsoft.editor.renderer.resources.ResourceManager
import com.badlogic.gdx.assets.AssetManager
import com.uwsoft.editor.renderer.utils.ItemWrapper
import gdx.scala.demo.character.Player
import gdx.scala.demo.components.{Collidable, Bullet}
import gdx.scala.demo.system.{CollisionSystem, BulletSystem}

class SpaceInvaders extends ApplicationAdapter {
  private var sceneLoader: SceneLoader = null
  private val viewPort: FitViewport = new FitViewport(7.5f, 5f)
  private var resourceManager: ResourceManager = null
  private var assetManager: AssetManager = null
  private var player: Player = null

  override def create() {
    loadScene()
    val root = new ItemWrapper(sceneLoader.getRoot)
    setPlayer(root)
    addSystems()
  }

  def addSystems() : Unit = {
    sceneLoader.addComponentsByTagName(Bullet.Tag, classOf[Bullet])
    sceneLoader.addComponentsByTagName(Collidable.Tag, classOf[Collidable])

    sceneLoader.getEngine.addSystem(BulletSystem(sceneLoader.getEngine, player))
    sceneLoader.getEngine.addSystem(CollisionSystem(sceneLoader.getEngine, player))
  }

  def setPlayer(root : ItemWrapper) : Unit = {
    player = new Player(sceneLoader.world)
    root.getChild("player").addScript(player)
  }

  def loadScene() : Unit = {
    sceneLoader = new SceneLoader
    sceneLoader.loadScene("MainScene", viewPort)
  }

  override def render() {
    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    sceneLoader.getEngine.update(Gdx.graphics.getDeltaTime)
  }


}