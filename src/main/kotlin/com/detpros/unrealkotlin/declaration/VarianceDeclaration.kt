package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.KModifier


/**
 *  Variance Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 5:43 PM EST )
 */
sealed class VarianceDeclaration {
    fun toModifier(): KModifier = when (this) {
        is In -> KModifier.IN
        is Out -> KModifier.OUT
    }

    fun isSameAs(other: VarianceDeclaration): Boolean = when (this) {
        is In -> other is In
        is Out -> other is Out
    }
    fun isSameAs(other: KModifier): Boolean = when (this) {
        is In -> other == KModifier.IN
        is Out -> other == KModifier.OUT
    }

    object In : VarianceDeclaration()
    object Out: VarianceDeclaration()

    companion object {
        fun fromModifier(modifier: KModifier): VarianceDeclaration = when (modifier) {
            KModifier.IN -> In
            KModifier.OUT -> Out
            else -> throw IllegalArgumentException("Modifier $modifier is not a variance modifier")
        }
    }

}