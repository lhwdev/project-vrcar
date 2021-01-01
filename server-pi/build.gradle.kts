import com.lhwdev.build.*
import org.hidetake.groovy.ssh.core.RunHandler
import org.hidetake.groovy.ssh.session.SessionHandler
import java.util.Properties


plugins {
	kotlin("jvm")
	id("com.github.johnrengelman.shadow") version "6.1.0"
	
	id("org.hidetake.ssh") version "2.10.1"
	
	id("common-plugin")
}

kotlin {
	setup()
}

dependencies {
	implementation(project(":json"))
	implementation(project(":backend-common"))
	implementation(project(":server-common"))
	
	implementation(serializationCore)
	implementation(coroutinesCore)
	
	implementation("com.pi4j:pi4j-core:1.2")
	implementation("com.github.mhashim6:Pi4K:0.1")
}


tasks {
	named<Jar>("shadowJar") {
		archiveFileName.set("main.jar")
		
		manifest {
			attributes("Main-Class" to "com.lhwdev.vrcar.rpi.MainKt")
		}
	}
	
	create<JavaExec>("run") {
		dependsOn("shadowJar")
		
		val jar = File(buildDir, "libs/main.jar")
		classpath(jar)
		
		doLast {
			ssh.run(delegateClosureOf<RunHandler> {
				session(remotes.getByName("rpi-remote"), delegateClosureOf<SessionHandler> {
					put(hashMapOf("from" to jar, "into" to "main.jar"))
					execute("java -jar main.jar")
				})
			})
		}
	}
}

remotes {
	create("rpi-remote") {
		user = "pi"
		host = "172.30.1.19"
		password = with(Properties()) {
			load(rootProject.file("local.properties").reader())
			getProperty("sshPassword")
		}
	}
}
