package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.utility.buildAll
import com.detpros.unrealkotlin.utility.builders
import com.detpros.unrealkotlin.corrections.models.*


/**
 *  Standard Declarations Correction
 *
 * @author IvanEOD ( 6/22/2023 at 1:20 PM EST )
 */
data class StandardDeclarationsCorrection(
    val commonPrefixReplacements: Map<String, String> = emptyMap(),
    val ignoreFunctions: List<String> = emptyList(),
    val ignoreProperties: List<String> = emptyList(),
    override val functions: List<FunctionCorrection> = emptyList(),
    override val properties: List<PropertyCorrection> = emptyList(),
    override val members: List<MemberCorrection> = emptyList(),
    override val classes: List<ClassCorrection> = emptyList(),
) : ClassCorrectionsProvider,
    CorrectionWithClasses<StandardDeclarationsCorrection, StandardDeclarationsCorrection.Builder>,
    CorrectionWithMembers<StandardDeclarationsCorrection, StandardDeclarationsCorrection.Builder> {

    override fun classConfigurations(): List<ClassCorrection> = classes()

    fun commonPrefixReplacements() = if (this == Default) commonPrefixReplacements
    else Default.commonPrefixReplacements + commonPrefixReplacements

    fun ignoreFunctionNames() = if (this == Default) ignoreFunctions
    else Default.ignoreFunctions + ignoreFunctions

    fun ignorePropertyNames() = if (this == Default) ignoreProperties
    else Default.ignoreProperties + ignoreProperties

    fun allMemberFunctions() = if (this == Default) functions
    else Default.functions + functions

    fun allMemberProperties() = if (this == Default) properties
    else Default.properties + properties

    fun allMembers() = if (this == Default) members
    else Default.members + members

    fun classes() = if (this == Default) classes
    else Default.classes + classes

    fun getAddOverrides() = classes()
        .filter { klass -> klass.functions.isNotEmpty() && klass.functions.any { it.shouldOverride == true } }
        .associate { klass ->
            klass.name to klass.functions.filter { it.shouldOverride == true }.map { it.name }.toSet()
        }

    fun definedPropertyRenames() = allMemberProperties()
        .filter { it.newName != null }
        .associate { it.name to it.newName!! }

    fun definedFunctionRenames() = allMemberFunctions()
        .filter { it.newName != null }
        .associate { it.name to it.newName!! }

    operator fun plus(other: StandardDeclarationsCorrection) =
        StandardDeclarationsCorrection(
            commonPrefixReplacements + other.commonPrefixReplacements,
            ignoreFunctions + other.ignoreFunctions,
            ignoreProperties + other.ignoreProperties,
            functions + other.functions,
            properties + other.properties,
            members + other.members,
            classes + other.classes
        )

    override fun toBuilder() = Builder(
        commonPrefixReplacements,
        ignoreFunctions,
        ignoreProperties,
        functions.builders(),
        properties.builders(),
        members.builders(),
        classes.builders()
    )

    class Builder(
        commonPrefixReplacements: Map<String, String> = emptyMap(),
        ignoreFunctions: List<String> = emptyList(),
        ignoreProperties: List<String> = emptyList(),
        allMemberFunctions: List<FunctionCorrection.Builder> = emptyList(),
        allMemberProperties: List<PropertyCorrection.Builder> = emptyList(),
        allMembers: List<MemberCorrection.Builder> = emptyList(),
        classes: List<ClassCorrection.Builder> = emptyList()
    ) : CorrectionWithMembers.Builder<StandardDeclarationsCorrection, Builder>,
        CorrectionWithClasses.Builder<StandardDeclarationsCorrection, Builder> {

        private val commonPrefixReplacements = commonPrefixReplacements.toMutableMap()
        private val ignoreFunctions = ignoreFunctions.toMutableSet()
        private val ignoreProperties = ignoreProperties.toMutableSet()
        private val functions = allMemberFunctions.toMutableList()
        private val properties = allMemberProperties.toMutableList()
        private val members = allMembers.toMutableList()
        private val classes = classes.toMutableList()

        override fun memberConfiguration(configuration: MemberCorrection.Builder) = apply {
            members.add(configuration)
        }

        override fun functionConfiguration(configuration: FunctionCorrection.Builder) = apply {
            functions.add(configuration)
        }

        override fun propertyConfiguration(configuration: PropertyCorrection.Builder) = apply {
            properties.add(configuration)
        }

        override fun classConfiguration(configuration: ClassCorrection.Builder) = apply {
            classes.add(configuration)
        }

        fun replaceCommonPrefix(prefix: String, replacement: String) = apply {
            commonPrefixReplacements[prefix] = replacement
        }

        fun ignoreFunction(name: String) = apply {
            ignoreFunctions.add(name)
        }

        fun ignoreFunctions(vararg name: String) = apply {
            ignoreFunctions.addAll(name)
        }

        fun ignoreProperty(name: String) = apply {
            ignoreProperties.add(name)
        }

        fun ignoreProperties(vararg name: String) = apply {
            ignoreProperties.addAll(name)
        }

        override fun include(other: StandardDeclarationsCorrection) = apply {
            commonPrefixReplacements.putAll(other.commonPrefixReplacements)
            ignoreFunctions.addAll(other.ignoreFunctions)
            ignoreProperties.addAll(other.ignoreProperties)
            functions.addAll(other.functions.builders())
            properties.addAll(other.properties.builders())
            members.addAll(other.members.map { it.toBuilder() })
            classes.addAll(other.classes.builders())
        }

        override fun include(other: Builder) = apply {
            commonPrefixReplacements.putAll(other.commonPrefixReplacements)
            ignoreFunctions.addAll(other.ignoreFunctions)
            ignoreProperties.addAll(other.ignoreProperties)
            functions.addAll(other.functions)
            properties.addAll(other.properties)
            members.addAll(other.members)
            classes.addAll(other.classes)
        }

        override fun build(): StandardDeclarationsCorrection = StandardDeclarationsCorrection(
            commonPrefixReplacements,
            ignoreFunctions.toList(),
            ignoreProperties.toList(),
            functions.buildAll(),
            properties.buildAll(),
            members.buildAll(),
            classes.buildAll()
        )

    }

    companion object {
        val Default by lazy { UnrealDeclarationsCorrection.Default.standardCorrections }
        val Empty by lazy { UnrealDeclarationsCorrection.Empty.standardCorrections }
    }
}