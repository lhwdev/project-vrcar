buildscript {
	val composeVersion = "0.3.0-build139"  /*"0.0.0-unmerged-build21"*/ // SwingPanel, used to 0.3.0-build139
	val kotlinVersion = "1.4.21"
	
	
	repositories {
		mavenCentral()
		google()
		jcenter()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
		maven("https://jitpack.io")
	}
	
	dependencies {
		classpath("org.jetbrains.compose:compose-gradle-plugin:$composeVersion")
		classpath(kotlin("gradle-plugin", version = kotlinVersion))
		classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
		classpath("com.android.tools.build:gradle:4.0.2")
	}
}

allprojects {
	repositories {
		mavenCentral()
		google()
		jcenter()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
		maven("https://jitpack.io")
	}
}
