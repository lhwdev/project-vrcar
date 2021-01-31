import com.lhwdev.build.*
import org.gradle.kotlin.dsl.dependencies

plugins {
	id("com.android.application")
	kotlin("android")
	
	id("common-plugin")
}


kotlin {
	setupAndroid()
}


dependencies {
	implementation(project(":client-common"))
	
	implementation("com.github.niqdev:mjpeg-view:1.7.0")
	
	val androidCompose = "1.0.0-alpha10"
	implementation("androidx.compose.ui:ui:$androidCompose")
	implementation("androidx.compose.foundation:foundation:$androidCompose")
	implementation("androidx.compose.material:material:$androidCompose")
	
	implementation("androidx.appcompat:appcompat:1.2.0")
	implementation("androidx.core:core-ktx:1.3.2")
	
	implementation(coroutinesCore)
}


android {
	compileSdkVersion(30)
	buildToolsVersion = "30.0.2"
	
	defaultConfig {
		applicationId = "com.lhwdev.vrcar.client"
		minSdkVersion(21)
		targetSdkVersion(30)
		versionCode = 1001
		versionName = "0.1"
		
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	
	buildTypes {
		named("release") {
			isMinifyEnabled = true
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	
	buildFeatures {
		compose = true
	}
	
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	
	composeOptions {
		kotlinCompilerVersion = "1.4.21"
		kotlinCompilerExtensionVersion = "stub-version" // see below
	}
}

configurations.all {
	resolutionStrategy.dependencySubstitution {
		substitute(module("androidx.compose:compose-compiler:stub-version"))
			.because("Working with old AGP")
			.with(module("androidx.compose.compiler:compiler:1.0.0-alpha10"))
	}
}


kotlin {
	target.compilations.all {
		kotlinOptions.useIR = true
	}
}
