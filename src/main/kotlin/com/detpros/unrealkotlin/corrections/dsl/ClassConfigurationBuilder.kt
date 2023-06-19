package com.detpros.unrealkotlin.corrections.dsl

import com.detpros.unrealkotlin.configuration.buildAll
import com.detpros.unrealkotlin.configuration.builders
import com.detpros.unrealkotlin.corrections.ClassCorrectionConfiguration


/**
 *  Class Configuration Builder
 *
 * @author IvanEOD ( 6/19/2023 at 11:46 AM EST )
 */

class ClassConfigurationBuilder(
    val name: String,
    var newName: String? = null,
    val superType: String? = null,
    var delete: Boolean = false,
    removeSupertypes: List<String> = emptyList(),
    addSupertypes: List<String> = emptyList(),
    members: List<MemberConfigurationBuilderImpl> = emptyList(),
    functions: List<FunctionConfigurationBuilder> = emptyList(),
    properties: List<PropertyConfigurationBuilder> = emptyList(),
): ConfigurationBuilderWithMembers<ClassCorrectionConfiguration, ClassConfigurationBuilder>  {
    private val _removeSupertypes = removeSupertypes.toMutableList()
    private val _addSupertypes = addSupertypes.toMutableList()
    private val _members = members.toMutableList()
    private val _functions = functions.toMutableList()
    private val _properties = properties.toMutableList()

    override fun include(other: ClassCorrectionConfiguration) = apply {
        newName = other.newName
        _removeSupertypes.addAll(other.removeSuperTypes)
        _addSupertypes.addAll(other.addSuperTypes)
        _members.addAll(other.members.builders())
        _functions.addAll(other.functions.builders())
        _properties.addAll(other.properties.builders())
    }

    override fun include(other: ClassConfigurationBuilder) = apply {
        newName = other.newName
        _removeSupertypes.addAll(other._removeSupertypes)
        _addSupertypes.addAll(other._addSupertypes)
        _members.addAll(other._members)
        _functions.addAll(other._functions)
        _properties.addAll(other._properties)
    }

    override fun memberConfiguration(configuration: MemberConfigurationBuilderImpl) = apply {
        _members.add(configuration)
    }

    override fun functionConfiguration(configuration: FunctionConfigurationBuilder) = apply {
        _functions.add(configuration)
    }

    override fun propertyConfiguration(configuration: PropertyConfigurationBuilder) = apply {
        _properties.add(configuration)
    }

    fun rename(newName: String) = apply { this.newName = newName }
    fun rename(block: ClassConfigurationBuilder.() -> String) = apply { rename(block()) }

    fun delete() = apply { delete = true }

    fun removeSupertype(types: List<String>) = apply { _removeSupertypes.addAll(types) }
    fun removeSupertype(vararg types: String) = removeSupertype(types.toList())
    fun removeSupertype(block: ClassConfigurationBuilder.() -> List<String>) = removeSupertype(block())

    fun addSupertype(types: List<String>) = apply { _addSupertypes.addAll(types) }
    fun addSupertype(vararg types: String) = addSupertype(types.toList())
    fun addSupertype(block: ClassConfigurationBuilder.() -> List<String>) = addSupertype(block())


    override fun build() = ClassCorrectionConfiguration(
        name,
        newName,
        superType,
        delete,
        _removeSupertypes,
        _addSupertypes,
        _members.buildAll(),
        _functions.buildAll(),
        _properties.buildAll()
    )

}

