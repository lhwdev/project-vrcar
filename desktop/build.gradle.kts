import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
	id("org.jetbrains.compose")
}

kotlin {
	jvm {
		withJava()
	}
	sourceSets {
		all {
			languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
		}

		named("jvmMain") {
			dependencies {
				implementation(compose.desktop.currentOs)
				implementation(project(":app"))
			}
		}
	}
}

compose.desktop {
	application {
		mainClass = "com.lhwdev.compose.vrcar.desktop.MainKt"
		
		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
			packageName = "VrCar Controller"
		}
	}
}
