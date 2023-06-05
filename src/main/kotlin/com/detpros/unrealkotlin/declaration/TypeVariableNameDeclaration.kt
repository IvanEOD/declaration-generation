package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.TypeVariableName


/**
 *  Type Variable Name Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 5:42 PM EST )
 */
sealed interface TypeVariableNameDeclaration : TypeNameDeclaration {
    val name: String
    val bounds: Set<TypeNameDeclaration>
    val variance: VarianceDeclaration?
    val annotations: Set<AnnotationDeclaration>
    val nullable: Boolean
    val reified: Boolean

    fun isSameAs(other: TypeVariableNameDeclaration): Boolean
    fun isSameAs(other: TypeVariableName): Boolean

    override fun toTypeName(): TypeVariableName

    companion object {

        fun fromTypeVariableName(typeVariableName: TypeVariableName): TypeVariableNameDeclaration = TypeVariableNameDeclarationImpl.fromTypeVariableName(typeVariableName)

    }

}


