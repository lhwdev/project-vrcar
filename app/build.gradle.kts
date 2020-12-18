import org.jetbrains.compose.compose

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.compose")
}

kotlin {
	jvm("desktop")
	
	sourceSets {
		all {
			languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
			languageSettings.useExperimentalAnnotation("kotlin.io.path.ExperimentalPathApi")
		}
		named("commonMain") {
			dependencies {
				api(compose.runtime)
				api(compose.foundation)
				api(compose.material)
			}
		}
		
		named("desktopMain") {
			dependencies {
				implementation(project(":backend"))
				implementation(compose.desktop.currentOs)
			}
		}
	}
}
