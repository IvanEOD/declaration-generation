package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.ParameterizedTypeName


/**
 *  Parameterized Type Name Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 5:39 PM EST )
 */
sealed interface ParameterizedTypeNameDeclaration : TypeNameDeclaration {

    val rawType: ClassNameDeclaration
    val typeArguments: Set<TypeNameDeclaration>
    val nullable: Boolean
    val annotations: Set<AnnotationDeclaration>

    fun isSameAs(other: ParameterizedTypeNameDeclaration): Boolean
    fun isSameAs(other: ParameterizedTypeName): Boolean

    override fun toTypeName(): ParameterizedTypeName

    companion object {
        fun fromParameterizedTypeName(parameterizedTypeName: ParameterizedTypeName): ParameterizedTypeNameDeclaration =
            ParameterizedTypeNameDeclarationImpl.fromParameterizedTypeName(parameterizedTypeName)
    }

}



