package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.CodeBlock


/**
 *  Code Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 10:59 AM EST )
 */
class CodeDeclarationImpl(override val code: CodeBlock): CodeDeclaration {
    override val members: Set<Declaration> = setOf()

    override fun isSameAs(other: CodeDeclaration): Boolean = code == other.code
    override fun isSameAs(other: CodeBlock): Boolean = code == other

    override fun refresh() {
        // Nothing to do here
    }
}