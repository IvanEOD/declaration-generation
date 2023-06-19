package com.detpros.unrealkotlin.corrections.dsl

import com.detpros.unrealkotlin.corrections.*


/**
 *  Member Configuration Builder
 *
 * @author IvanEOD ( 6/19/2023 at 11:50 AM EST )
 */

sealed class MemberConfigurationBuilder<T : BaseMemberCorrectionConfiguration<T, B>, B : MemberConfigurationBuilder<T, B>> : RenameableConfigurationBuilder<T, B> {
    abstract override val name: String
    abstract override var newName: String?
    abstract override fun include(other: T): B
    abstract override fun include(other: B): B
    abstract override fun build(): T
}

sealed interface RenameableConfigurationBuilder<T : ICorrectionConfiguration<T, B>, B : RenameableConfigurationBuilder<T, B>>: ICorrectionConfigurationBuilder<T, B> {
    val name: String
    var newName: String?
    fun rename(newName: String): B = (this as B).apply { this.newName = newName }
    fun rename(block: B.() -> String): B = rename(block(this as B))
}

class MemberConfigurationBuilderImpl(
    override val name: String,
    override var newName: String? = null
): MemberConfigurationBuilder<MemberCorrectionConfiguration, MemberConfigurationBuilderImpl>() {
    override fun include(other: MemberCorrectionConfiguration) = apply {
        if (other.name == name && other.newName != null) newName = other.newName
    }
    override fun include(other: MemberConfigurationBuilderImpl) = apply {
        if (other.name == name && other.newName != null) newName = other.newName
    }
    override fun build(): MemberCorrectionConfiguration = MemberCorrectionConfiguration(name, newName)
}

class PropertyConfigurationBuilder(
    override val name: String,
    override var newName: String? = null,
    val type: String? = null,
    var newType: String? = null,
    var shouldOverride: Boolean? = null
): MemberConfigurationBuilder<PropertyCorrectionConfiguration, PropertyConfigurationBuilder>() {
    override fun include(other: PropertyCorrectionConfiguration) = apply {
        if (other.name == name) {
            other.newName?.let { newName = it }
            other.newType?.let { newType = it }
            other.shouldOverride?.let { shouldOverride = it }
        }
    }

    override fun include(other: PropertyConfigurationBuilder) = apply {
        if (other.name == name) {
            other.newName?.let { newName = it }
            other.newType?.let { newType = it }
            other.shouldOverride?.let { shouldOverride = it }
        }
    }

    fun changeType(newType: String) = apply { this.newType = newType }
    fun changeType(block: PropertyConfigurationBuilder.() -> String) = changeType(block())

    fun shouldOverride(shouldOverride: Boolean = true) = apply { this.shouldOverride = shouldOverride }
    fun shouldOverride(block: PropertyConfigurationBuilder.() -> Boolean) = shouldOverride(block())

    override fun build(): PropertyCorrectionConfiguration  = PropertyCorrectionConfiguration(
        name,
        newName,
        type,
        newType,
        shouldOverride
    )

}

class FunctionConfigurationBuilder(
    override val name: String,
    override var newName: String? = null,
    val returnType: String? = null,
    var newReturnType: String? = null,
    var shouldOverride: Boolean? = null,
    var removeTypeVariables: Boolean? = null,
    renameParameters: Map<String, String> = emptyMap(),
    removeParameters: List<String> = emptyList(),
    addParameters: Map<String, String> = emptyMap()
): MemberConfigurationBuilder<FunctionCorrectionConfiguration, FunctionConfigurationBuilder>() {
    private val _renameParameters = renameParameters.toMutableMap()
    private val _removeParameters = removeParameters.toMutableList()
    private val _addParameters = addParameters.toMutableMap()

    fun changeReturnType(newReturnType: String) = apply { this.newReturnType = newReturnType }
    fun changeReturnType(block: FunctionConfigurationBuilder.() -> String) = apply { changeReturnType(block()) }
    fun shouldOverride(shouldOverride: Boolean = true) = apply { this.shouldOverride = shouldOverride }
    fun shouldOverride(block: FunctionConfigurationBuilder.() -> Boolean) = apply { shouldOverride(block()) }
    fun removeTypeVariables(removeTypeVariables: Boolean = true) = apply { this.removeTypeVariables = removeTypeVariables }
    fun removeTypeVariables(block: FunctionConfigurationBuilder.() -> Boolean) = apply { removeTypeVariables(block()) }

    fun removeParameter(name: String) = apply { _removeParameters.add(name) }

    fun renameParameter(oldName: String, newName: String) = apply { _renameParameters[oldName] = newName }
    fun renameParameter(block: FunctionConfigurationBuilder.() -> Pair<String, String>) = renameParameter(block().first, block().second)

    fun addParameter(name: String, type: String) = apply { _addParameters[name] = type }
    fun addParameter(block: FunctionConfigurationBuilder.() -> Pair<String, String>) = addParameter(block().first, block().second)

    override fun include(other: FunctionCorrectionConfiguration) = apply {
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

    override fun include(other: FunctionConfigurationBuilder) = apply {
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

    override fun build(): FunctionCorrectionConfiguration = FunctionCorrectionConfiguration(
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