package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName


/**
 *  Super Interface Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:44 PM EST )
 */
sealed interface SuperInterfaceDeclaration : Declaration {

    val type: TypeNameDeclaration
    val delegate: CodeDeclaration?

    fun isSameAs(other: SuperInterfaceDeclaration): Boolean
    fun isSameAs(other: Pair<TypeName, CodeBlock?>): Boolean
    fun isSameAs(other: Map.Entry<TypeName, CodeBlock?>): Boolean

    fun toSuperInterface(): Pair<TypeName, CodeBlock?>

    companion object {
        fun fromSuperInterface(typeName: TypeNameDeclaration, codeBlock: CodeDeclaration?): SuperInterfaceDeclaration =
            SuperInterfaceDeclarationImpl.fromSuperInterface(typeName, codeBlock)
        fun fromSuperInterface(typeName: TypeName, codeBlock: CodeBlock?): SuperInterfaceDeclaration =
            SuperInterfaceDeclarationImpl.fromSuperInterface(typeName, codeBlock)
        fun fromSuperInterface(entry: Map.Entry<TypeName, CodeBlock?>): SuperInterfaceDeclaration = fromSuperInterface(entry.key, entry.value)
    }
}

