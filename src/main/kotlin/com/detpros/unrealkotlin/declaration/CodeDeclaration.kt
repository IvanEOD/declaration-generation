package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.CodeBlock


/**
 *  Code Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:43 PM EST )
 */
sealed interface CodeDeclaration : Declaration {

    val code: CodeBlock

    fun isSameAs(other: CodeDeclaration): Boolean
    fun isSameAs(other: CodeBlock): Boolean

    fun toCodeBlock(): CodeBlock = code

    companion object {
        fun fromCodeBlock(codeBlock: CodeBlock): CodeDeclaration = CodeDeclarationImpl(codeBlock)
    }

}

