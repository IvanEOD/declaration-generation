package com.detpros.unrealkotlin.utility

import com.squareup.kotlinpoet.ClassName


/**
 *  Generation Environment
 *
 * @author IvanEOD ( 5/24/2023 at 3:06 PM EST )
 */
object GenerationEnvironment {

    object AutoImports {
        private val _autoImports = mutableMapOf<String, ClassName>()
        operator fun get(key: String): ClassName? = _autoImports[key]
        operator fun set(key: String, value: ClassName) {
            _autoImports[key] = value
        }
        operator fun set(key: String, value: String) = set(key, ClassName.bestGuess(value))
    }

    fun autoImport(name: String): ClassName? = AutoImports[name]

    fun addAutoImport(name: String, type: String) {
        AutoImports[name] = type
    }
    fun addAutoImport(className: ClassName) {
        AutoImports[className.simpleName] = className
    }



    init {
        addAutoImport("Any", "kotlin.Any")
        addAutoImport("Array", "kotlin.Array")
        addAutoImport("Boolean", "kotlin.Boolean")
        addAutoImport("Byte", "kotlin.Byte")
        addAutoImport("Char", "kotlin.Char")
        addAutoImport("Double", "kotlin.Double")
        addAutoImport("Float", "kotlin.Float")
        addAutoImport("Int", "kotlin.Int")
        addAutoImport("Long", "kotlin.Long")
        addAutoImport("Short", "kotlin.Short")
        addAutoImport("String", "kotlin.String")
        addAutoImport("Unit", "kotlin.Unit")
        addAutoImport("Nothing", "kotlin.Nothing")
        addAutoImport("List", "kotlin.collections.List")
        addAutoImport("Map", "kotlin.collections.Map")
        addAutoImport("Set", "kotlin.collections.Set")
        addAutoImport("MutableList", "kotlin.collections.MutableList")
        addAutoImport("MutableMap", "kotlin.collections.MutableMap")
        addAutoImport("MutableSet", "kotlin.collections.MutableSet")
        addAutoImport("Iterable", "kotlin.collections.Iterable")
        addAutoImport("Iterator", "kotlin.collections.Iterator")
        addAutoImport("Sequence", "kotlin.sequences.Sequence")
        addAutoImport("ArrayBuffer", "org.khronos.webgl.ArrayBuffer")
        addAutoImport("Console", "kotlin.js.Console")
        addAutoImport("JsName", "kotlin.js.JsName")
        addAutoImport("EventListenerOptions", "org.w3c.dom.EventListenerOptions")
        addAutoImport("AddEventListenerOptions", "org.w3c.dom.AddEventListenerOptions")
        addAutoImport("EventListener", "org.w3c.dom.events.EventListener")
    }


}