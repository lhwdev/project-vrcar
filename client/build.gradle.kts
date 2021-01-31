import com.lhwdev.build.*
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.compose")
	
	id("common-plugin")
}


kotlin {
	val jvmTarget = setupJvm("jvm")
	setupJvm("desktop", dependsOn = setOf(jvmTarget))
	setupCommon()
	
	dependencies {
		compileOnly(compose.runtime)
		compileOnly(compose.foundation)
		compileOnly(compose.material)
	}
	
	dependencies("jvmMain") {
		implementation(project(":client-common"))
		
		implementation("com.github.sarxos:webcam-capture:0.3.12")
		implementation("com.github.sarxos:webcam-capture-driver-ipcam:0.3.12")
	}
	
	dependencies("desktopMain") {
		implementation(compose.desktop.currentOs)
	}
}


// desktop

compose.desktop {
	application {
		disableDefaultConfiguration()
		from(kotlin.targets.getByName("desktop"))
		mainClass = "com.lhwdev.vrcar.client.desktop.MainKt"
		
		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
			packageName = "VrCar Client"
		}
	}
}
