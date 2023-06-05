package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.PropertySpec


/**
 *  Property Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:44 PM EST )
 */
sealed interface PropertyDeclaration : DeclarationWithJsName, DeclarationWithModifiers {

    val type: TypeNameDeclaration
    val receiverType: TypeNameDeclaration?
    val typeVariables: Set<TypeVariableNameDeclaration>
    val initializer: CodeDeclaration?
    val getter: FunctionDeclaration?
    val setter: FunctionDeclaration?

    val isMutable: Boolean
    val isDelegated: Boolean

    fun isType(type: String): Boolean
    fun isType(type: TypeNameDeclaration): Boolean
    fun changeType(type: String)
    fun changeType(type: TypeNameDeclaration)

    fun isSameAs(other: PropertyDeclaration): Boolean
    fun isSameAs(other: PropertySpec): Boolean

    fun toPropertySpec(): PropertySpec

    companion object {

        const val MakeAllMutable = true

        fun fromPropertySpec(propertySpec: PropertySpec): PropertyDeclaration =
            PropertyDeclarationImpl.fromPropertySpec(propertySpec)
    }
}

