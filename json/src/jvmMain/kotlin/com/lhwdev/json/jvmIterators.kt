package com.lhwdev.json

import java.io.IOException
import java.io.Reader
import java.io.Writer


// note that this implementation does not support concurrency
fun Reader.asIterator() = object : CloneableCharIterator() {
	private var last = 0
	private var hasLast = false
	private var hasNext = true
	
	private fun eof(): Nothing = throw IOException("EOF")
	
	override fun nextChar() = when {
		!hasNext -> eof()
		hasLast -> {
			hasLast = false
			last
		}
		else -> try {
			read()
		} catch(e: IOException) {
			hasNext = false
			throw e
		}
	}.let { read ->
		val char = read.toChar()
		if(char.toInt() != read) eof()
		char
	}
	
	override fun hasNext() = when {
		!hasNext -> false
		hasLast -> {
			last >= 0
		}
		else -> {
			try {
				val read = read()
				hasLast = true
				last = read
				read >= 0
			} catch(e: IOException) {
				hasLast = false
				false
			}
		}
	}
	
	override val cloneSupported get() = false
	override fun cloneHere() = error("Not implemented")
}

fun Writer.asCharOutput() = object : CharOutput {
	private val writer = this@asCharOutput
	override fun write(char: Char) {
		writer.write(char.toInt())
		writer.flush()
	}
	
	override fun write(text: CharSequence) {
		writer.write(text.toString())
		writer.flush()
	}
}
