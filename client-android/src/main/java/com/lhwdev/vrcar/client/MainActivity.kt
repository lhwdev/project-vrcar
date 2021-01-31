package com.lhwdev.vrcar.client

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.ui.platform.setContent


val AmbientActivity = ambientOf<Activity>()


class MainActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		setContent {
			Providers(AmbientActivity provides this) {
				App()
			}
		}
	}
}
