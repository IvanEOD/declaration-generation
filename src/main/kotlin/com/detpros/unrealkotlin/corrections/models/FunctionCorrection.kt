package com.detpros.unrealkotlin.corrections.models

import com.detpros.unrealkotlin.declaration.ClassNameDeclaration
import com.detpros.unrealkotlin.declaration.DeclarationWithName
import com.detpros.unrealkotlin.declaration.FunctionDeclaration
import com.detpros.unrealkotlin.declaration.FunctionDeclarationImpl
import com.squareup.kotlinpoet.KModifier


/**
 *  Function Correction
 *
 * @author IvanEOD ( 6/22/2023 at 12:18 PM EST )
 */

data class FunctionCorrection(
    override val name: String = "",
    override val newName: String? = null,
    val returnType: String? = null,
    val newReturnType: String? = null,
    val shouldOverride: Boolean? = null,
    val removeTypeVariables: Boolean? = null,
    val renameParameters: Map<String, String> = emptyMap(),
    val removeParameters: List<String> = emptyList(),
    val addParameters: Map<String, String> = emptyMap(),
) : BaseMemberCorrection<FunctionCorrection, FunctionCorrection.Builder>() {

    private fun isTarget(declaration: FunctionDeclaration): Boolean {
        if (declaration.originalName != name) return false
        return !(returnType != null && !declaration.returnType?.isName(returnType)!!)
    }

    override fun correct(declaration: DeclarationWithName) {
        if (declaration !is FunctionDeclaration) return
        declaration as FunctionDeclarationImpl
        if (!isTarget(declaration)) return
        super.correct(declaration)

        if (newReturnType != null) declaration._returnType = ClassNameDeclaration.getClassName(newReturnType)
        if (removeTypeVariables == true) declaration.removeTypeVariables()
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

        renameParameters.forEach { (oldName, newName) ->
            declaration.changeParameterType(oldName, newName)
        }

        removeParameters.forEach { parameterName ->
            declaration.deleteParameter(parameterName)
        }

        addParameters.forEach { (parameterName, parameterType) ->
            declaration.addParameter(parameterName, ClassNameDeclaration.getClassName(parameterType))
        }

    }

    override fun toBuilder() = Builder(
        name,
        newName,
        returnType,
        newReturnType,
        shouldOverride,
        removeTypeVariables,
        renameParameters,
        removeParameters,
        addParameters
    )

    class Builder(
        override val name: String,
        override var newName: String? = null,
        val returnType: String? = null,
        var newReturnType: String? = null,
        var shouldOverride: Boolean? = null,
        var removeTypeVariables: Boolean? = null,
        renameParameters: Map<String, String> = emptyMap(),
        removeParameters: List<String> = emptyList(),
        addParameters: Map<String, String> = emptyMap()
    ): BaseMemberCorrection.Builder<FunctionCorrection, Builder>() {
        private val _renameParameters = renameParameters.toMutableMap()
        private val _removeParameters = removeParameters.toMutableList()
        private val _addParameters = addParameters.toMutableMap()

        fun changeReturnType(newReturnType: String) = apply { this.newReturnType = newReturnType }
        fun changeReturnType(block: Builder.() -> String) = apply { changeReturnType(block()) }
        fun shouldOverride(shouldOverride: Boolean = true) = apply { this.shouldOverride = shouldOverride }
        fun shouldOverride(block: Builder.() -> Boolean) = apply { shouldOverride(block()) }
        fun removeTypeVariables(removeTypeVariables: Boolean = true) = apply { this.removeTypeVariables = removeTypeVariables }
        fun removeTypeVariables(block: Builder.() -> Boolean) = apply { removeTypeVariables(block()) }

        fun removeParameter(name: String) = apply { _removeParameters.add(name) }

        fun renameParameter(oldName: String, newName: String) = apply { _renameParameters[oldName] = newName }
        fun renameParameter(block: Builder.() -> Pair<String, String>) = renameParameter(block().first, block().second)

        fun addParameter(name: String, type: String) = apply { _addParameters[name] = type }
        fun addParameter(block: Builder.() -> Pair<String, String>) = addParameter(block().first, block().second)

        override fun include(other: FunctionCorrection) = apply {
            if (other.name == name) {
                other.newName?.let { newName = it }
                other.newReturnType?.let { newReturnType = it }
                other.shouldOverride?.let { shouldOverride = it }
                other.removeTypeVariables?.let { removeTypeVariables = it }
                _renameParameters.putAll(other.renameParameters)
                _removeParameters.addAll(other.removeParameters)
                _addParameters.putAll(other.addParameters)
            }
        }

        override fun include(other: Builder) = apply {
            if (other.name == name) {
                other.newName?.let { newName = it }
                other.newReturnType?.let { newReturnType = it }
                other.shouldOverride?.let { shouldOverride = it }
                other.removeTypeVariables?.let { removeTypeVariables = it }
                _renameParameters.putAll(other._renameParameters)
                _removeParameters.addAll(other._removeParameters)
                _addParameters.putAll(other._addParameters)
            }
        }

        override fun build(): FunctionCorrection = FunctionCorrection(
            name,
            newName,
            returnType,
            newReturnType,
            shouldOverride,
            removeTypeVariables,
            _renameParameters,
            _removeParameters,
            _addParameters
        )

    }

}