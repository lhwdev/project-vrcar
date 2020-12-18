
plugins {
	kotlin("jvm")
	id("kotlinx-serialization")
}

kotlin {
	sourceSets {
		all {
			languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
			languageSettings.useExperimentalAnnotation("kotlin.io.path.ExperimentalPathApi")
		}
	}
}

dependencies {
	implementation(project(":json"))
	
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
}
