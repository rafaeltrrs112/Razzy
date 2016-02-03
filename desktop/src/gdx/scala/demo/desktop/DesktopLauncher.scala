package gdx.scala.demo.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import gdx.scala.demo.GdxScalaDemoGame


object DesktopLauncher {
  def main(arg: Array[String]) {
    val config: LwjglApplicationConfiguration = new LwjglApplicationConfiguration
    config.width = 1280
    config.height = 800
    new LwjglApplication(new GdxScalaDemoGame, config)

  }
}