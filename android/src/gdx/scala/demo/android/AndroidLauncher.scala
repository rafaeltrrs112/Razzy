package gdx.scala.demo.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import gdx.scala.demo.SpaceInvaders

class AndroidLauncher extends AndroidApplication {
  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val config: AndroidApplicationConfiguration = new AndroidApplicationConfiguration
    initialize(new SpaceInvaders, config)
  }
}