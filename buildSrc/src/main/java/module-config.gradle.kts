import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

project.afterEvaluate {
  with(extensions) {
    findByType<AppExtension>()?.androidAppConfig()
    findByType<LibraryExtension>()?.androidLibraryConfig()
    findByType<TestedExtension>()?.androidCommonConfig()
  }
  commonConfig()
}

fun TestedExtension.androidCommonConfig() {
  setCompileSdkVersion(AndroidSdk.compileSdk)

  defaultConfig {
    minSdk = AndroidSdk.minSdk
    targetSdk = AndroidSdk.targetSdk

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
    }

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    testInstrumentationRunnerArguments["clearPackageData"] = "true"

    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }

  packagingOptions {
    resources {
      excludes += listOf(
        "META-INF/AL2.0",
        "META-INF/LGPL2.1"
      )
    }
  }
}

fun AppExtension.androidAppConfig() {
  defaultConfig {
    applicationId = AppCoordinates.APP_ID
    versionCode = AppCoordinates.VERSION_CODE
    versionName = AppCoordinates.VERSION_NAME
  }
}

fun LibraryExtension.androidLibraryConfig() {
  buildFeatures {
    buildConfig = false
  }
}

fun Project.commonConfig() {

  extensions.findByType<KotlinProjectExtension>()?.apply {
    // FIXME: Android not supported yet https://youtrack.jetbrains.com/issue/KT-37652
    explicitApi()
  }

  tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
  }

  tasks.withType<Test> {
    maxParallelForks = Runtime.getRuntime().availableProcessors() * 2
    testLogging {
      events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
    useJUnitPlatform()
  }
}
