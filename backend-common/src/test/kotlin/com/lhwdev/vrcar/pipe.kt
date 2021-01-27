package com.lhwdev.vrcar

import java.io.IOException
import java.io.Reader
import java.io.Writer
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.atomic.AtomicBoolean


private const val sMinByte = Char.MIN_VALUE.toInt()
private const val sMaxByte = Char.MAX_VALUE.toInt()
private inline fun Int.toCharOr(block: () -> Nothing): Char =
	if(this in sMinByte..sMaxByte) this.toChar() else block()


fun pipedCharStream(): Pair<Reader, Writer> {
	val queue = SynchronousQueue<Char>()
	val closed = AtomicBoolean(false)
	
	return object : Reader() {
		override fun read(cbuf: CharArray, off: Int, len: Int): Int {
			for(i in 0 until len) {
				cbuf[i + off] = read().toCharOr { return i }
			}
			return len
		}
		
		override fun read() = if(closed.get()) queue.poll().toInt() else -1
		override fun close() {
			closed.set(true)
		}
	} to object : Writer() {
		override fun close() {
			closed.set(true)
		}
		
		override fun flush() {
		}
		
		override fun write(cbuf: CharArray, off: Int, len: Int) {
			for(i in off until off + len) {
				write(cbuf[i])
			}
		}
		
		fun write(c: Char) {
			if(closed.get()) throw IOException("EOF")
			queue.offer(c)
		}
		
		override fun write(c: Int) {
			write(c.toChar())
		}
	}
}
