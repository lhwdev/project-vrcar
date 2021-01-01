package com.lhwdev.vrcar.rpi

import com.pi4j.io.gpio.Pin
import mhashim6.pi4k.digitalOutput
import mhashim6.pi4k.softPwmOutput
import kotlin.math.abs



class Motor(first: Pin, second: Pin, enable: Pin) {
	private val firstOut = softPwmOutput(first)
	private val secondOut = softPwmOutput(second)
	private val enable = digitalOutput(enable)
	
	
	/**
	 * From -1 to 1.
	 */
	var force: Float = 0f
		set(value) {
			field = value
			update()
		}
	
	fun update() {
		when(force) {
			0f -> enable.low()
			in 0f..1f -> {
				firstOut.pwm = (force * 100).toInt() // 0..100
				secondOut.pwm = 0
				enable.high()
			}
			in -1f..0f -> {
				firstOut.pwm = 0
				secondOut.pwm = (abs(force) * 100).toInt()
				enable.high()
			}
		}
	}
}
