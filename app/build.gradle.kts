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
				implementation(compose.desktop.currentOs)
			}
		}
	}
}

dependencies {
//	kotlinCompilerPluginClasspath(files("D:\\LHW\\asm\\app\\new\\com.asmx.core\\dump-ir\\compiler-plugin\\build\\libs\\compiler-plugin.jar"))
}
