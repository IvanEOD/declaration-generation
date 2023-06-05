package com.detpros.unrealkotlin.declaration


/**
 *  Parent Declaration
 *
 * @author IvanEOD ( 5/26/2023 at 7:58 AM EST )
 */
internal sealed class ParentDeclaration : Declaration {

    @JvmName("claimChild")
    fun Declaration.claim() {
        when (val child = this@claim) {
            is ChildDeclaration -> {
                this@ParentDeclaration.parentClaim(child)
                this@ParentDeclaration.onChildClaimed(child)
            }
            else -> {}
        }
    }

    @JvmName("releaseChild")
    fun Declaration.release() {
        when (val child = this@release) {
            is ChildDeclaration -> {
                this@ParentDeclaration.parentRelease(child)
                this@ParentDeclaration.onChildReleased(child)
            }
            else -> {}
        }
    }

    fun updateChildren() {
        members.filterIsInstance<ChildDeclaration>().forEach { it.claim() }
    }

    private fun parentClaim(declaration: ChildDeclaration) {
        if (declaration._parent != this) {
            declaration._parent?.parentRelease(declaration)
            declaration._parent = this
        }
    }

    private fun parentRelease(declaration: ChildDeclaration) {
        if (declaration._parent == this) declaration._parent = null
    }


    abstract fun onChildClaimed(declaration: ChildDeclaration)
    abstract fun onChildReleased(declaration: ChildDeclaration)

    private fun handleOwnership(declaration: ChildDeclaration, claim: Boolean = true) {
        if (claim) declaration.claim() else declaration.release()
    }

}