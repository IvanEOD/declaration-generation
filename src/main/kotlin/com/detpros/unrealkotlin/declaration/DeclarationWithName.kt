package com.detpros.unrealkotlin.declaration


/**
 *  Named Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:42 PM EST )
 */
sealed interface DeclarationWithName : Declaration, Comparable<DeclarationWithName> {

    val originalName: String
    val name: String

    fun lockRenaming()
    fun unlockRenaming()
    val isRenamingLocked: Boolean

    fun rename(name: String)

    val isTopLevel: Boolean get() = false

    override fun compareTo(other: DeclarationWithName): Int = when (this) {
        is FileDeclaration -> 4
        is ClassDeclaration -> 3
        is TypeAliasDeclaration -> 3
        is FunctionDeclaration -> 2
        is PropertyDeclaration -> 1
        is ParameterDeclaration -> 0
    }
}