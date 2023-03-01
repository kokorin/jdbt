package com.github.kokorin.jdbt.template.pebble

import io.pebbletemplates.pebble.extension.writer.SpecializedWriter
import io.pebbletemplates.pebble.extension.writer.StringWriterSpecializedAdapter
import java.io.StringWriter
import java.io.Writer

class JdbtSpecializedWriter : Writer(), SpecializedWriter {
    private val writer = StringWriter()
    private val specialized = StringWriterSpecializedAdapter(writer)


    override fun write(o: Any?) {
        TODO()
    }

    override fun write(cbuf: CharArray, off: Int, len: Int) = writer.write(cbuf, off, len)

    override fun close() = writer.close()

    override fun flush() = writer.flush()

    override fun writeSpecialized(i: Int) = specialized.writeSpecialized(i)

    override fun writeSpecialized(l: Long) = specialized.writeSpecialized(l)

    override fun writeSpecialized(d: Double) = specialized.writeSpecialized(d)

    override fun writeSpecialized(f: Float) = specialized.writeSpecialized(f)

    override fun writeSpecialized(s: Short) = specialized.writeSpecialized(s)

    override fun writeSpecialized(b: Byte) = specialized.writeSpecialized(b)

    override fun writeSpecialized(c: Char) = specialized.writeSpecialized(c)

    override fun writeSpecialized(s: String?) = specialized.writeSpecialized(s)

    override fun toString(): String = writer.toString()
}