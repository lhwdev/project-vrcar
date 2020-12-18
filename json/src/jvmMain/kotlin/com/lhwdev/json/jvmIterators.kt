package com.lhwdev.json

import java.io.IOException
import java.io.Reader
import java.io.Writer


// note that this implementation does not support concurrency
fun Reader.asIterator() = object : CloneableCharIterator() {
	private var last = 0
	override fun nextChar(): Char {
		val read: Int
		if(last < 0) {
			read = read()
			last = read
		}
		else {
			read = last
			last = -1
		}
		val char = last.toChar()
		if(char.toInt() != last) throw IOException("couldn't read char")
		return char
	}
	
	override fun hasNext(): Boolean {
		last = read()
		return last >= 0
	}
	
	override val cloneSupported get() = false
	override fun cloneHere() = error("Not implemeneted")
}

fun Writer.asCharOutput() = object : CharOutput {
	private val writer = this@asCharOutput
	override fun write(char: Char) {
		writer.write(char.toInt())
	}
	
	override fun write(text: CharSequence) {
		writer.write(text.toString())
	}
}
