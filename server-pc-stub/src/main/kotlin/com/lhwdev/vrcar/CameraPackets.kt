package com.lhwdev.vrcar

import kotlinx.serialization.Serializable


@Serializable
sealed class CameraRequestPacket


@Serializable
object GetFrame : CameraRequestPacket()


@Serializable
sealed class CameraResponsePacket


// following bytes streaming for frame
@Serializable
data class Frame(val timeStamp: Long) : CameraResponsePacket()
