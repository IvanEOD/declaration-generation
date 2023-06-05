package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.AnnotationSpec


/**
 *  Annotation Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:43 PM EST )
 */
sealed interface AnnotationDeclaration : Declaration {
    val type: TypeNameDeclaration
    val arguments: Set<CodeDeclaration>

    val isJsName: Boolean
    fun getJsName(): String

    fun isSameAs(other: AnnotationDeclaration): Boolean
    fun isSameAs(other: AnnotationSpec): Boolean

    fun toAnnotationSpec(): AnnotationSpec

    companion object {
        fun jsName(name: String): AnnotationDeclaration = AnnotationDeclarationImpl.jsName(name)
        fun fromAnnotationSpec(annotationSpec: AnnotationSpec): AnnotationDeclaration =
            AnnotationDeclarationImpl.fromAnnotationSpec(annotationSpec)
    }

}

