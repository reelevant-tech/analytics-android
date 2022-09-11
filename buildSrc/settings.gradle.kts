pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
  plugins {
    id("de.fayard.refreshVersions") version "0.23.0"
////                            # available:"0.30.0"
////                            # available:"0.30.1"
////                            # available:"0.30.2"
////                            # available:"0.40.0"
////                            # available:"0.40.1"
////                            # available:"0.40.2"
////                            # available:"0.50.0"
  }
}

buildscript {
  repositories { gradlePluginPortal() }
}

plugins {
  id("de.fayard.refreshVersions")
}
