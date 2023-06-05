package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.DYNAMIC
import com.squareup.kotlinpoet.Dynamic
import com.squareup.kotlinpoet.TypeName


/**
 *  Dynamic Type Name Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 5:55 PM EST )
 */
sealed interface DynamicTypeNameDeclaration : TypeNameDeclaration {

    override fun isName(name: String): Boolean = name == "dynamic"

    override fun toTypeName(): Dynamic = DYNAMIC

    fun isSameAs(other: DynamicTypeNameDeclaration): Boolean = true

    companion object : DynamicTypeNameDeclaration {

        override val members: Set<Declaration> = emptySet()
        override fun refresh() {
            // Do nothing
        }

        fun fromDynamic(typeName: Dynamic): DynamicTypeNameDeclaration = this
        override fun allNames(): List<String> = listOf("dynamic")
        override fun isSameAs(typeName: TypeName): Boolean = typeName == DYNAMIC
        override fun isSameAs(other: TypeNameDeclaration): Boolean = other is DynamicTypeNameDeclaration
    }

}