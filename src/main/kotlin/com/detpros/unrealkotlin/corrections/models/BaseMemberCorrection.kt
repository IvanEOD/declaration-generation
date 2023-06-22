package com.detpros.unrealkotlin.corrections.models

import com.detpros.unrealkotlin.declaration.DeclarationWithName


/**
 *  Base Member Correction
 *
 * @author IvanEOD ( 6/22/2023 at 12:07 PM EST )
 */
sealed class BaseMemberCorrection<T : BaseMemberCorrection<T, B>, B : BaseMemberCorrection.Builder<T, B>> : RenameableCorrection<T, B>() {
    abstract override val name: String
    abstract override val newName: String?
    open fun correct(declaration: DeclarationWithName) {
        if (declaration.originalName == name) {
            if (newName != null) {
                declaration.rename("memberConfiguration", newName!!)
                declaration.lockRenaming()
            }
        }
    }

    sealed class Builder<T : BaseMemberCorrection<T, B>, B : Builder<T, B>> : RenameableCorrection.Builder<T, B>() {
        abstract override val name: String
        abstract override var newName: String?
        abstract override fun include(other: T): B
        abstract override fun include(other: B): B
        abstract override fun build(): T
    }

}
