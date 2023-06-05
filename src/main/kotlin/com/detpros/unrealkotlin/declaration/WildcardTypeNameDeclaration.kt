package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.WildcardTypeName


/**
 *  Wildcard Type Name Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 5:51 PM EST )
 */
sealed interface WildcardTypeNameDeclaration : TypeNameDeclaration {

    val inTypes: Set<TypeNameDeclaration>
    val outTypes: Set<TypeNameDeclaration>
    val annotations: Set<AnnotationDeclaration>
    val nullable: Boolean


    fun isSameAs(other: WildcardTypeNameDeclaration): Boolean
    fun isSameAs(other: WildcardTypeName): Boolean

    override fun toTypeName(): WildcardTypeName


    companion object {
        fun fromWildcardTypeName(wildcardTypeName: WildcardTypeName): WildcardTypeNameDeclaration =
            WildcardTypeNameDeclarationImpl.fromWildcardTypeName(wildcardTypeName)
    }


}


