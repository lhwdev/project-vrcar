import com.lhwdev.build.*


plugins {
	kotlin("jvm")
	id("kotlinx-serialization")
	id("com.github.johnrengelman.shadow") version "6.1.0"
	id("common-plugin")
}

kotlin {
	setupCommon()
}

dependencies {
	implementation(project(":backend-common"))
	implementation(project(":server-common"))
	implementation(project(":json"))
	
	implementation("org.bytedeco:opencv-platform:4.4.0-1.5.4")
	implementation("org.bytedeco:javacv:1.5.4")
	implementation("org.bytedeco:javacv-platform:1.5.4")
	
	implementation("io.ktor:ktor-server-core:1.5.0")
	implementation("io.ktor:ktor-server-netty:1.5.0")
	implementation("io.ktor:ktor-html-builder:1.5.0")
	
	implementation(coroutinesCore)
	implementation(serializationCore)
}


tasks.named<Jar>("jar") {
	dependsOn("shadowJar")
}
