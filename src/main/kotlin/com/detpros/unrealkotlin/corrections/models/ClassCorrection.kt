package com.detpros.unrealkotlin.corrections.models

import com.detpros.unrealkotlin.utility.buildAll
import com.detpros.unrealkotlin.utility.builders
import com.detpros.unrealkotlin.declaration.*
import com.detpros.unrealkotlin.declaration.ClassDeclarationImpl


/**
 *  Class Correction
 *
 * @author IvanEOD ( 6/22/2023 at 12:22 PM EST )
 */

data class ClassCorrection(
    val name: String = "",
    val newName: String? = null,
    val superType: String? = null,
    val delete: Boolean = false,
    val removeSuperTypes: List<String> = emptyList(),
    val addSuperTypes: List<String> = emptyList(),
    override val members: List<MemberCorrection> = emptyList(),
    override val functions: List<FunctionCorrection> = emptyList(),
    override val properties: List<PropertyCorrection> = emptyList(),
) : CorrectionWithMembers<ClassCorrection, ClassCorrection.Builder> {

    private fun ClassDeclaration.isTargetName(): Boolean = originalName == this@isTargetName.name || originalName == newName
    private fun ClassDeclaration.isSuperType(): Boolean = superType != null && hasSuperType(superType)

    fun correct(declaration: ClassDeclaration) {
        declaration as ClassDeclarationImpl
        var isTarget = true
        if (!declaration.isTargetName()) isTarget = declaration.isSuperType()
        if (!isTarget) {
            println("Correction tried on invalid class: ${declaration.originalName} != $name${if (superType != null) " && does not have supertype: $superType" else ""}")
            return
        }
        if (newName != null) {
            declaration.rename("classConfiguration", newName)
            declaration.lockRenaming()
        }
        if (removeSuperTypes.isNotEmpty()) {
            removeSuperTypes.forEach {
                if (declaration.superclass.isName(it)) declaration._superclass = TypeNameDeclaration.Any
                else {
                    declaration.superinterfaces
                        .filter { superInterface -> superInterface.type.isName(it) }
                        .forEach { superInterface -> declaration.removeSuperinterface(superInterface) }
                }
            }
        }
        if (addSuperTypes.isNotEmpty()) {
            addSuperTypes.forEach {
                val className = ClassNameDeclaration.getClassName(it)
                declaration.addSuperinterface(className)
            }
        }

        declaration.functions.forEach { function ->
            val memberConfig = memberConfig(function.originalName)
            memberConfig?.correct(function)
            val functionConfig = functionConfig(function.originalName)
            functionConfig?.correct(function)
        }
        declaration.properties.forEach { property ->
            val memberConfig = memberConfig(property.originalName)
            memberConfig?.correct(property)
            val propertyConfig = propertyConfig(property.originalName, property.type.allNames().lastOrNull())
            propertyConfig?.correct(property)
        }

    }

    override fun toBuilder() = Builder(
        name,
        newName,
        superType,
        delete,
        removeSuperTypes,
        addSuperTypes,
        members.builders(),
        functions.builders(),
        properties.builders()
    )


    class Builder(
        val name: String,
        var newName: String? = null,
        val superType: String? = null,
        var delete: Boolean = false,
        removeSupertypes: List<String> = emptyList(),
        addSupertypes: List<String> = emptyList(),
        members: List<MemberCorrection.Builder> = emptyList(),
        functions: List<FunctionCorrection.Builder> = emptyList(),
        properties: List<PropertyCorrection.Builder> = emptyList(),
    ): CorrectionWithMembers.Builder<ClassCorrection, Builder> {
        private val _removeSupertypes = removeSupertypes.toMutableList()
        private val _addSupertypes = addSupertypes.toMutableList()
        private val _members = members.toMutableList()
        private val _functions = functions.toMutableList()
        private val _properties = properties.toMutableList()

        override fun include(other: ClassCorrection) = apply {
            newName = other.newName
            _removeSupertypes.addAll(other.removeSuperTypes)
            _addSupertypes.addAll(other.addSuperTypes)
            _members.addAll(other.members.builders())
            _functions.addAll(other.functions.builders())
            _properties.addAll(other.properties.builders())
        }

        override fun include(other: Builder) = apply {
            newName = other.newName
            _removeSupertypes.addAll(other._removeSupertypes)
            _addSupertypes.addAll(other._addSupertypes)
            _members.addAll(other._members)
            _functions.addAll(other._functions)
            _properties.addAll(other._properties)
        }

        override fun memberConfiguration(configuration: MemberCorrection.Builder) = apply {
            _members.add(configuration)
        }

        override fun functionConfiguration(configuration: FunctionCorrection.Builder) = apply {
            _functions.add(configuration)
        }

        override fun propertyConfiguration(configuration: PropertyCorrection.Builder) = apply {
            _properties.add(configuration)
        }

        fun rename(newName: String) = apply { this.newName = newName }
        fun rename(block: Builder.() -> String) = apply { rename(block()) }

        fun delete() = apply { delete = true }

        fun removeSupertype(types: List<String>) = apply { _removeSupertypes.addAll(types) }
        fun removeSupertype(vararg types: String) = removeSupertype(types.toList())
        fun removeSupertype(block: Builder.() -> List<String>) = removeSupertype(block())

        fun addSupertype(types: List<String>) = apply { _addSupertypes.addAll(types) }
        fun addSupertype(vararg types: String) = addSupertype(types.toList())
        fun addSupertype(block: Builder.() -> List<String>) = addSupertype(block())


        override fun build() = ClassCorrection(
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


}
