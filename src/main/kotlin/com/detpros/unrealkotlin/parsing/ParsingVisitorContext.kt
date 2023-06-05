package com.detpros.unrealkotlin.parsing

import com.squareup.kotlinpoet.FileSpec
import java.util.concurrent.ConcurrentLinkedDeque

class ParsingVisitorContext {
    private data class Bookmark(val name: String)

    val files = ConcurrentLinkedDeque<FileSpec>()
    internal val stack = ConcurrentLinkedDeque<Any>()
    var defaultPackageName = ""
    fun register(file: FileSpec) = files.add(file)
    fun push(value: Any) {
        stack.push(value)
    }

    fun <T> pop(): T = stack.pop() as T
    fun <T> peek(): T = (if (!stack.isEmpty()) stack.peek() else null) as T
    fun bookmark(name: String) {
        push(Bookmark(name))
    }

    fun popToBookmark(): List<Any> {
        val values = mutableListOf<Any>()
        while (peek<Any>() !is Bookmark) {
            values += pop<Any>()
        }
        val slab = pop<Bookmark>()
        return values.reversed()
    }

    override fun toString() = if (!stack.isEmpty()) peek<Any>().toString() else "{}"
}