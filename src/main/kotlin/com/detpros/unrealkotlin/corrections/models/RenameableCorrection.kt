package com.detpros.unrealkotlin.corrections.models


/**
 *  Renameable Correction
 *
 * @author IvanEOD ( 6/22/2023 at 12:44 PM EST )
 */
sealed class RenameableCorrection<T : RenameableCorrection<T, B>, B : RenameableCorrection.Builder<T, B>> : Correction<T, B> {

    abstract val name: String
    abstract val newName: String?

    abstract class Builder<T : RenameableCorrection<T, B>, B : Builder<T, B>> :
        Correction.Builder<T, B> {
        abstract val name: String
        abstract var newName: String?
        fun rename(newName: String): B = (this as B).apply { this.newName = newName }
        fun rename(block: B.() -> String): B = rename(block(this as B))
    }

}