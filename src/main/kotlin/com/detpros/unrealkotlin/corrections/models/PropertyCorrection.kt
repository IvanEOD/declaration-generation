package com.detpros.unrealkotlin.corrections.models

import com.detpros.unrealkotlin.declaration.DeclarationWithName
import com.detpros.unrealkotlin.declaration.PropertyDeclaration
import com.detpros.unrealkotlin.declaration.PropertyDeclarationImpl
import com.squareup.kotlinpoet.KModifier


/**
 *  Property Correction
 *
 * @author IvanEOD ( 6/22/2023 at 12:17 PM EST )
 */

data class PropertyCorrection(
    override val name: String = "",
    override val newName: String? = null,
    val type: String? = null,
    val newType: String? = null,
    val shouldOverride: Boolean? = null
) : BaseMemberCorrection<PropertyCorrection, PropertyCorrection.Builder>() {

    private fun isTarget(declaration: PropertyDeclaration): Boolean {
        if (declaration.originalName != name) return false
        if (type == null) return true
        return declaration.type.isName(type)
    }
    override fun correct(declaration: DeclarationWithName) {
        if (declaration !is PropertyDeclaration) return
        declaration as PropertyDeclarationImpl
        if (!isTarget(declaration)) return
        super.correct(declaration)
        if (newType != null) declaration.changeType(newType)
        when (shouldOverride) {
            true -> {
                if (!declaration.isOverride) {
                    declaration.addModifier(KModifier.OVERRIDE)
                    if (declaration.isJsNamePresent) declaration.removeJsName()
                }
            }

            false -> if (declaration.isOverride) declaration.removeModifier(KModifier.OVERRIDE)
            else -> {}
        }
    }

    override fun toBuilder() = Builder(
        name,
        newName,
        type,
        newType,
        shouldOverride
    )

    class Builder(
        override val name: String,
        override var newName: String? = null,
        val type: String? = null,
        var newType: String? = null,
        var shouldOverride: Boolean? = null
    ): BaseMemberCorrection.Builder<PropertyCorrection, Builder>() {
        override fun include(other: PropertyCorrection) = apply {
            if (other.name == name) {
                other.newName?.let { newName = it }
                other.newType?.let { newType = it }
                other.shouldOverride?.let { shouldOverride = it }
            }
        }

        override fun include(other: Builder) = apply {
            if (other.name == name) {
                other.newName?.let { newName = it }
                other.newType?.let { newType = it }
                other.shouldOverride?.let { shouldOverride = it }
            }
        }

        fun changeType(newType: String) = apply { this.newType = newType }
        fun changeType(block: Builder.() -> String) = changeType(block())

        fun shouldOverride(shouldOverride: Boolean = true) = apply { this.shouldOverride = shouldOverride }
        fun shouldOverride(block: Builder.() -> Boolean) = shouldOverride(block())

        override fun build(): PropertyCorrection = PropertyCorrection(
            name,
            newName,
            type,
            newType,
            shouldOverride
        )

    }


}