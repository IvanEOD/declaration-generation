package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.ParameterSpec


/**
 *  Parameter Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:44 PM EST )
 */
sealed interface ParameterDeclaration : DeclarationWithAnnotations, DeclarationWithModifiers, DeclarationWithName {

    val type: TypeNameDeclaration

    fun isVariableType(): Boolean

    fun setTypeVariableToBound()
    fun changeType(typeName: String)
    fun changeType(typeName: TypeNameDeclaration)
    fun removeTypeVariable(typeVariable: TypeVariableNameDeclaration)

    fun isSameAs(declaration: ParameterDeclaration): Boolean
    fun isSameAs(spec: ParameterSpec): Boolean

    fun toParameterSpec(): ParameterSpec

    companion object {
        fun fromParameterSpec(parameterSpec: ParameterSpec): ParameterDeclaration =
            ParameterDeclarationImpl.fromParameterSpec(parameterSpec)
    }
}
