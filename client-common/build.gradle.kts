import com.lhwdev.build.*


plugins {
	kotlin("jvm")
	id("kotlinx-serialization")
	
	id("common-plugin")
}

kotlin {
	setupCommon()
}

dependencies {
	implementation(project(":util"))
	implementation(project(":backend-common"))
	implementation(project(":json"))
	
	implementation("com.github.sarxos:webcam-capture:0.3.12")
	implementation("com.github.sarxos:webcam-capture-driver-ipcam:0.3.12")
	
	implementation(coroutinesCore)
	implementation(serializationCore)
	implementation(serializationJson)
}
