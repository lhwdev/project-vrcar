package com.lhwdev.vrcar.rpi

import com.pi4j.io.gpio.Pin
import com.pi4j.io.gpio.RaspiBcmPin
import kotlin.math.min


data class CarMotorPins(
	val m1: Pin = RaspiBcmPin.GPIO_20,
	val m2: Pin = RaspiBcmPin.GPIO_21,
	val pwmA: Pin = RaspiBcmPin.GPIO_26,
	
	val m3: Pin = RaspiBcmPin.GPIO_06,
	val m4: Pin = RaspiBcmPin.GPIO_13,
	val pwmB: Pin = RaspiBcmPin.GPIO_12
)


class CarMotor(pins: CarMotorPins = CarMotorPins()) {
	private val motorLeft = Motor(pins.m1, pins.m2, pins.pwmA)
	private val motorRight = Motor(pins.m3, pins.m4, pins.pwmB)
	
	var speed: Float = 0f
		set(value) {
			field = value
			update()
		}
	
	var steeringDifference: Float = 0f
		set(value) {
			field = value
			update()
		}
	
	var steeringForward: Float = 0f
		set(value) {
			field = value
			update()
		}
	
	
	fun reset() {
		speed = 0f
		steeringDifference = 0f
		steeringForward = 0f
	}
	
	
	/**
	 * Let speed be 1. If steering becomes +1, it do not move forward or backward, instead it rotates right as fast
	 * as it can. If steering becomes -1, it rotates left without advancing. If steering >= 0, left motor force is set
	 * to 1, and steering <= 0, right motor force to 1.
	 *
	 * Considering speed, overall force is multiplied by speed.
	 */
	private fun calculateOutput(
		speed: Float,
		steeringDifference: Float, steeringForward: Float
	): Pair<Float, Float> {
		// 1. let speed be 1
		val left = min(steeringDifference * 2 * (1 - steeringForward) + 1, 1f)
		val right = min(steeringDifference * -2 * (1 - steeringForward) + 1, 1f)
		
		return left * speed to right * speed
	}
	
	fun update() {
		val (aPower, bPower) = calculateOutput(speed, steeringDifference, steeringForward)
		motorLeft.force = aPower
		motorRight.force = bPower
	}
}
