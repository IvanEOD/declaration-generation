package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.KModifier


/**
 *  Declaration With Modifiers
 *
 * @author IvanEOD ( 5/24/2023 at 11:03 AM EST )
 */
sealed interface DeclarationWithModifiers : Declaration {

    val modifiers: Set<KModifier>

    val isOverride: Boolean get() = hasModifier(KModifier.OVERRIDE)
    val isExternal: Boolean get() = hasModifier(KModifier.EXTERNAL)
    val isAbstract: Boolean get() = hasModifier(KModifier.ABSTRACT)

    fun setModifiers(modifiers: Collection<KModifier>)
    fun setModifiers(declarationWithModifiers: DeclarationWithModifiers) = setModifiers(declarationWithModifiers.modifiers)

    fun addModifier(modifier: KModifier)
    fun removeModifier(modifier: KModifier)
    fun hasModifier(modifier: KModifier): Boolean = modifier in modifiers
    fun hasModifiers(vararg modifiers: KModifier) = modifiers.all { hasModifier(it) }

}