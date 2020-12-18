plugins {
	id("com.android.library")
	kotlin("multiplatform")
	id("kotlinx-serialization")
}

kotlin {
	android()
	jvm("desktop")
	
	sourceSets {
		all {
			languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
			languageSettings.useExperimentalAnnotation("kotlin.io.path.ExperimentalPathApi")
		}
		named("commonMain") {
			dependencies {
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
				implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0")
				implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
			}
		}
		
		named("desktopMain") {
			dependencies {
			}
		}
		named("androidMain") {
			dependencies {
			}
		}
	}
}

android {
	compileSdkVersion(30)
	
	defaultConfig {
		minSdkVersion(21)
		targetSdkVersion(30)
		versionCode = 1
		versionName = "1.0"
	}
	
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	
	sourceSets {
		named("main") {
			manifest.srcFile("src/androidMain/AndroidManifest.xml")
			res.srcDirs("src/androidMain/res")
		}
	}
}
