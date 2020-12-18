package com.lhwdev.json


interface CharOutput {
	fun write(char: Char)
	fun write(text: CharSequence) {
		for(c in text) write(c)
	}
}


fun StringBuilder.toCharOutput() = object : CharOutput {
	override fun write(char: Char) {
		append(char)
	}
	
	override fun write(text: CharSequence) {
		append(text)
	}
}

fun CharOutput.dumping() = object : CharOutput {
	val o = this@dumping
	override fun write(char: Char) {
		o.write(char)
		print(char)
	}
	
	override fun write(text: CharSequence) {
		o.write(text)
		print(text)
	}
}