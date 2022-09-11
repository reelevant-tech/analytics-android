plugins {
  `kotlin-dsl`
}

repositories {
  google()
  mavenCentral()
}

dependencies {
  implementation(GradlePlugin.kotlin)
  implementation(GradlePlugin.android)
}

object GradlePlugin {
  const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:_"
  const val android = "com.android.tools.build:gradle:_"
}
