package com.lhwdev.vrcar

import kotlinx.serialization.Serializable


@Serializable
sealed class Packet


/**
 * This packet is sent as soon as the connection is established.
 */
@Serializable
object HelloPacket : Packet()

/**
 * Check if the connection is lasting.
 * When received this, you should reply with identical [Ping].
 */
@Serializable
object Ping : Packet()


@Serializable
sealed class Movement : Packet()

/**
 * Change the moving speed of the car. [speed] is an absolute value between -1 and 1. (inclusive; `speed in -1..1`)
 *
 * You can put zero speed to stop the car.
 * Zero speed signifies that the car goes backward.
 */
@Serializable
data class Speed(val speed: Float) : Movement()

/**
 * Changes the steering of the car. [difference] is an absolute value between -1 and 1
 * (inclusive; `difference in -1..1`) and [forward] is an absolute value between 0 and 1 (inclusive).
 *
 * Zero steering difference signifies it won't rotate left or right. when positive, it will turn right and left on
 * negative.
 * If [forward] is 1, it won't rotate. If [forward] is 0, it won't go forward or backward, rather it will literally
 * rotate like a wheel.
 */
@Serializable
data class Steer(val difference: Float, val forward: Float) : Movement()

@Serializable
data class RawMotor(val left: Float, val right: Float) : Movement()


@Serializable
sealed class Sense : Packet()

// @Serializable
// data class CameraHint
