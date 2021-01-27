package com.lhwdev.vrcar.rpi

import com.pi4j.io.gpio.*
import mhashim6.pi4k.pwm
import mhashim6.pi4k.pwmOutput
import mhashim6.pi4k.softPwmOutput


private val sInitPwm: Unit = run {
	// pwm()
}


fun anyPwmOutput(
	pin: Pin,
	defaultValue: Int = 0,
	name: String = pin.name,
	provider: GpioProvider = GpioFactory.getDefaultProvider(),
	controller: GpioController = GpioFactory.getInstance()
): GpioPinPwmOutput {
	sInitPwm
	
	val output =
		if(PinMode.PWM_OUTPUT in pin.supportedPinModes) pwmOutput(pin, defaultValue, name, provider, controller)
		else softPwmOutput(pin, defaultValue, name, provider, controller)
	output.setPwmRange(100)
	return output
}
