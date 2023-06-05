package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.FunSpec


/**
 *  Function Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:46 PM EST )
 */
sealed interface FunctionDeclaration : DeclarationWithJsName, DeclarationWithModifiers, DeclarationWithAnnotations {

    val receiverType: TypeNameDeclaration?
    val returnType: TypeNameDeclaration?
    val typeVariables: Set<TypeVariableNameDeclaration>
    val parameters: Set<ParameterDeclaration>
    val delegateConstructor: String?
    val delegateConstructorArguments: Set<CodeDeclaration>

    fun hasTypeVariables(): Boolean
    fun removeTypeVariables()

    fun changeParameterType(name: String, type: TypeNameDeclaration)
    fun changeParameterType(name: String, type: String)

    fun isSameAs(other: FunctionDeclaration): Boolean
    fun isSameAs(other: FunSpec): Boolean

    fun toFunSpec(): FunSpec

    companion object {
        fun fromFunSpec(functionSpec: FunSpec): FunctionDeclaration = FunctionDeclarationImpl.fromFunSpec(functionSpec)
    }
}

