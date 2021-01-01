import com.lhwdev.build.*


plugins {
	kotlin("jvm")
	id("common-plugin")
}

kotlin {
	setupCommon()
}

dependencies {
	implementation(project(":backend-common"))
	implementation(project(":json"))
	implementation(coroutinesCore)
	implementation(serializationCore)
	implementation(serializationJson)
}
