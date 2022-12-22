package dev.emortal.nbstom

import java.nio.ByteBuffer

class NBSInstrument(val name: String, val soundFile: String, val pitch: Byte, val press: Boolean) {
    companion object {
        fun readInstrument(buffer: ByteBuffer) =
            NBSInstrument(
                buffer.getNBSString(),
                buffer.getNBSString(),
                buffer.get(),
                buffer.get().toInt() == 1
            )
    }
}