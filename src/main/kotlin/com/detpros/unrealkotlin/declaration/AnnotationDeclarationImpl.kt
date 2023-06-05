package com.detpros.unrealkotlin.declaration


import com.detpros.unrealkotlin.utility.isSameAs
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import org.jetbrains.kotlin.utils.addToStdlib.applyIf


/**
 *  Annotation Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 10:59 AM EST )
 */
internal class AnnotationDeclarationImpl(
    type: TypeNameDeclaration,
    arguments: Collection<CodeDeclaration> = emptySet(),
) : AnnotationDeclaration {

    private var _type: TypeNameDeclaration = type
    private val _arguments = arguments.toMutableSet()

    override val type: TypeNameDeclaration get() = _type
    override val arguments: Set<CodeDeclaration> get() = _arguments
    override val members: Set<Declaration> get() = setOf(type) + arguments

    override val isJsName: Boolean get() = toAnnotationSpec().toString().contains("JsName")
    override fun getJsName(): String = toAnnotationSpec().toString().substringAfter("\"").substringBeforeLast("\"")

    override fun isSameAs(other: AnnotationDeclaration): Boolean =
        type.isSameAs(other.type) &&
        arguments.isSameAs(other.arguments, CodeDeclaration::isSameAs)

    override fun isSameAs(other: AnnotationSpec): Boolean =
        type.isSameAs(other.typeName.toDeclaration()) &&
        arguments.isSameAs(other.members.map(CodeDeclaration::fromCodeBlock), CodeDeclaration::isSameAs)

    private var _annotationSpec: AnnotationSpec? = null

    override fun toAnnotationSpec(): AnnotationSpec {
        if (_annotationSpec == null) _annotationSpec = when (val typeValue = _type) {
                is ClassNameDeclaration -> AnnotationSpec.builder(typeValue.toTypeName())
                is ParameterizedTypeNameDeclaration -> AnnotationSpec.builder(typeValue.toTypeName())
                else -> throw IllegalStateException("Annotation type must be ClassName or ParameterizedTypeName")
            }
                .applyIf(_arguments.isNotEmpty()) { arguments.fold(this) { builder, argument -> builder.addMember(argument.toCodeBlock()) } }
                .build()
        return _annotationSpec!!
    }

    override fun refresh() {
        _annotationSpec = null
        members.forEach(Declaration::refresh)
    }

    companion object {
        fun jsName(name: String): AnnotationDeclaration = fromAnnotationSpec(
            AnnotationSpec.builder(ClassName("kotlin.js", "JsName"))
                .addMember("%L", "\"$name\"")
                .build()
        )

        fun fromAnnotationSpec(annotationSpec: AnnotationSpec): AnnotationDeclaration = AnnotationDeclarationImpl(
            annotationSpec.typeName.toDeclaration(),
            annotationSpec.members.map(CodeDeclaration::fromCodeBlock)
        )
    }

}