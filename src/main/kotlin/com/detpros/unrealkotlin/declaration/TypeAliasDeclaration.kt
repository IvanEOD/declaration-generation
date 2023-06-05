package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.TypeAliasSpec


/**
 *  Type Alias Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:44 PM EST )
 */
sealed interface TypeAliasDeclaration : DeclarationWithName, DeclarationWithModifiers, DeclarationWithAnnotations {

    val typeVariables: Set<TypeVariableNameDeclaration>
    val type: TypeNameDeclaration

    fun isSameAs(other: TypeAliasDeclaration): Boolean
    fun isSameAs(other: TypeAliasSpec): Boolean

    fun toTypeAliasSpec(): TypeAliasSpec

    companion object {

        fun fromTypeAliasSpec(typeAliasSpec: TypeAliasSpec): TypeAliasDeclaration = TypeAliasDeclarationImpl.fromTypeAliasSpec(typeAliasSpec)

    }

}
