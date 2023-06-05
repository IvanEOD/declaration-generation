package com.detpros.unrealkotlin.declaration


/**
 *  Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:41 PM EST )
 */
@DeclarationMarker
sealed interface Declaration {

    val members: Set<Declaration>

    val allMembers: Set<Declaration> get() = members + members.flatMap { it.allMembers }

    fun refresh()

}

