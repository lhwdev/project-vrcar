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
	
	implementation(coroutinesCore)
	implementation(serializationCore)
	implementation(serializationJson)
}
