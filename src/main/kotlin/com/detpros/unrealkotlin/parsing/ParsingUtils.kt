package com.detpros.unrealkotlin.parsing

import com.squareup.kotlinpoet.*


data class Accessor(val function: FunSpec)
data class Mutator(val function: FunSpec)
data class Delegate(val delegation: CodeBlock)
data class Import(val packageName: String, val shortName: String, val alias: String?)
class TypeProjection(val type: TypeName, val variance: KModifier?)
class SuperCall {
    lateinit var type: TypeName
    val arguments: MutableList<CodeBlock> = mutableListOf()
}
class SuperType {
    lateinit var type: TypeName
    var delegate: CodeBlock? = null
}

data class ImportDirective(
    val className: ClassName,
    val packageName: String,
    val name: String,
    val alias: String?
)
sealed class CallBlock {
    val arguments = mutableListOf<CodeBlock>()
    class ThisCallBlock : CallBlock()
    class SuperCallBlock : CallBlock()
}
enum class ClassType {
    ANNOTATION,
    CLASS,
    ENUM,
    INTERFACE,
    OBJECT
}