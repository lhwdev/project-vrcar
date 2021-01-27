import com.lhwdev.build.*
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat


plugins {
	kotlin("multiplatform")
	id("org.jetbrains.compose")
	
	id("common-plugin")
}

kotlin {
	setupJvm("desktop")
	setupCommon()
	
	dependencies {
		api(compose.runtime)
		api(compose.foundation)
		api(compose.material)
	}
	
	dependencies("desktopMain") {
		implementation(project(":client-common"))
		
		implementation("com.github.sarxos:webcam-capture:0.3.12")
		implementation("com.github.sarxos:webcam-capture-driver-ipcam:0.3.12")
		implementation(compose.desktop.currentOs)
	}
}

compose.desktop {
	application {
		mainClass = "com.lhwdev.vrcar.client.desktop.MainKt"
		
		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
			packageName = "VrCar Client"
		}
	}
}
