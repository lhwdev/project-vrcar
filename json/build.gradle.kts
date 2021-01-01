import com.lhwdev.build.*


plugins {
	kotlin("multiplatform")
	id("kotlinx-serialization")
	
	id("common-plugin")
}

kotlin {
	setupJvm()
	setupCommon()
	
	dependencies {
		implementation(coroutinesCore)
		implementation(serializationCore)
		implementation(serializationJson)
	}
}
