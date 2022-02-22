package dev.emortal.nbstom

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets

fun ByteBuffer.getNBSString(): String {
    val length = int
    val bytes = ByteArray(length)
    this[bytes]
    val byteBuffer = ByteBuffer.wrap(bytes)
    val charBuffer: CharBuffer = StandardCharsets.UTF_8.decode(byteBuffer)
    val string = CharArray(length)
    charBuffer[string]
    return String(string)
}

val ByteBuffer.unsignedShort: Int
    get() {
        val bytes = ByteArray(2)
        this[bytes]
        var integerVal = 0
        for (i in bytes.indices.reversed()) {
            val b = bytes[i]
            integerVal += java.lang.Byte.toUnsignedInt(b) shl i * 8
        }
        return integerVal
    }

fun readUnsignedInt(buffer: ByteBuffer): Long {
    val bytes = ByteArray(4)
    buffer[bytes]
    var longVal: Long = 0
    for (i in bytes.indices.reversed()) {
        val b = bytes[i]
        longVal += (java.lang.Byte.toUnsignedInt(b) shl i * 8).toLong()
    }
    return longVal
}