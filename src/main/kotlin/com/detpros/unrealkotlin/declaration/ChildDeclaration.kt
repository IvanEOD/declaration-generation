package com.detpros.unrealkotlin.declaration


/**
 *  Child Declaration
 *
 * @author IvanEOD ( 5/26/2023 at 8:23 AM EST )
 */
internal sealed interface ChildDeclaration : Declaration {

    var _parent: ParentDeclaration?
    val parent: ParentDeclaration get() = _parent ?: throw IllegalStateException("Parent is not set for (${this::class.simpleName}) $this")

}
