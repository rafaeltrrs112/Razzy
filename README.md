# Space Invaders Scala

Simple Space Invaders implementation in Scala using LibGDX and Ashley ECS.

## Setup

Create a 'local.properties' which sets 'sdk.dir' to the path of your Android sdk.

Example: `sdk.dir=/opt/android/sdk`


```
-keep class com.badlogic.gdx.math.** { *; }

-keep class com.badlogic.gdx.physics.bullet.collision.CollisionJNI { *; }
-keep class com.badlogic.gdx.physics.bullet.dynamics.DynamicsJNI { *; }
-keep class com.badlogic.gdx.physics.bullet.extras.ExtrasJNI { *; }
-keep class com.badlogic.gdx.physics.bullet.linearmath.LinearMathJNI { *; }
-keep class com.badlogic.gdx.physics.bullet.softbody.SoftbodyJNI { *; }

-keep class com.badlogic.gdx.physics.bullet.collision.btBroadphaseAabbCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.btBroadphaseRayCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.btConvexTriangleCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.btInternalTriangleIndexCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.btNodeOverlapCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.btOverlapCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.btOverlapFilterCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.btOverlappingPairCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.btTriangleCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.btTriangleConvexcastCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.btTriangleRaycastCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.ContactCache { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.ContactListener { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.CustomCollisionDispatcher { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.LocalRayResult { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.LocalShapeInfo { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.RayResultCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.AllHitsRayResultCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.ConvexResultCallback { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.LocalConvexResult { *; }
-keep class com.badlogic.gdx.physics.bullet.collision.ContactResultCallback { *; }

-keep class com.badlogic.gdx.physics.bullet.dynamics.InternalTickCallback { *; }

-keep class com.badlogic.gdx.physics.bullet.extras.btBulletWorldImporter { *; }

-keep class com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw { *; }
-keep class com.badlogic.gdx.physics.bullet.linearmath.btMotionState { *; }
```
