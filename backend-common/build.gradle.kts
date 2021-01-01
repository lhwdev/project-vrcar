import com.lhwdev.build.*


plugins {
	kotlin("jvm")
	id("kotlinx-serialization")
	
	id("common-plugin")
}

kotlin {
	setup()
}

dependencies {
	implementation(project(":util"))
	implementation(project(":json"))
	
	implementation(coroutinesCore)
	implementation(serializationCore)
	implementation(serializationJson)
}
