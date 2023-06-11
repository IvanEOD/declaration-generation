package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.declaration.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.squareup.kotlinpoet.KModifier
import java.io.File
import java.net.URL


/**
 *  Correction Configuration
 *
 * @author IvanEOD ( 6/5/2023 at 10:04 AM EST )
 */


data class CorrectionConfiguration(
    val enumCorrections: EnumCorrectionsConfiguration = EnumCorrectionsConfiguration(),
    val nonClassMemberCorrections: NonClassMemberCorrectionsConfiguration = NonClassMemberCorrectionsConfiguration(),
    val standardCorrections: StandardCorrectionsConfiguration = StandardCorrectionsConfiguration(),
    val unnamedClasses: UnnamedClassesConfiguration = UnnamedClassesConfiguration()
) : ClassConfigurationsProvider {

    override fun classConfigurations(): List<ClassCorrectionConfiguration> =
        enumCorrections.classConfigurations() +
                unnamedClasses.classConfigurations()

    operator fun plus(other: CorrectionConfiguration) =
        CorrectionConfiguration(
            enumCorrections + other.enumCorrections,
            nonClassMemberCorrections + other.nonClassMemberCorrections,
            standardCorrections + other.standardCorrections,
            unnamedClasses + other.unnamedClasses
        )

    companion object {

        private fun String.toFile() = File(this)

        private inline fun <reified T> String.parseYaml(): T {
            val objectMapper = ObjectMapper(YAMLFactory())
            return objectMapper.readValue(this, T::class.java)
        }

        fun loadFromYaml(yaml: String): CorrectionConfiguration = yaml.parseYaml()
        fun loadFromFile(file: File): CorrectionConfiguration = file.readText().parseYaml()
        fun loadFromFile(path: String): CorrectionConfiguration = loadFromFile(path.toFile())
        fun File.loadCorrectionConfiguration(): CorrectionConfiguration = loadFromFile(this)


        val Default: CorrectionConfiguration by lazy {
            downloadTextFromUrl(DefaultConfigUrl).parseYaml()
        }

        val Empty: CorrectionConfiguration by lazy {
            downloadTextFromUrl(EmptyConfigUrl).parseYaml()
        }

        private fun downloadTextFromUrl(url: String): String = URL(url).readText()

        private const val DefaultConfigUrl =
            "https://raw.githubusercontent.com/IvanEOD/declaration-generation/main/src/main/resources/defaultUnrealKtConfiguration.yml"
        private const val EmptyConfigUrl =
            "https://raw.githubusercontent.com/IvanEOD/declaration-generation/main/src/main/resources/emptyUnrealKtConfiguration.yml"
    }

}

data class EnumCorrectionsConfiguration(
    val classes: List<ClassCorrectionConfiguration> = emptyList(),
) : ClassConfigurationsProvider {
    override fun classConfigurations(): List<ClassCorrectionConfiguration> = if (this == Default) classes
    else Default.classes + classes

    operator fun plus(other: EnumCorrectionsConfiguration) =
        EnumCorrectionsConfiguration(classes + other.classes)

    companion object {
        val Default by lazy { CorrectionConfiguration.Default.enumCorrections }
        val Empty by lazy { CorrectionConfiguration.Empty.enumCorrections }
    }
}

data class NonClassMemberCorrectionsConfiguration(
    val typeAliasRenames: Map<String, String> = emptyMap(),
    val propertyRenames: Map<String, String> = emptyMap()
) {

    fun typeAliasRenames() = if (this == Default) typeAliasRenames
    else Default.typeAliasRenames + typeAliasRenames

    fun propertyRenames() = if (this == Default) propertyRenames
    else Default.propertyRenames + propertyRenames

    operator fun plus(other: NonClassMemberCorrectionsConfiguration) =
        NonClassMemberCorrectionsConfiguration(
            typeAliasRenames + other.typeAliasRenames,
            propertyRenames + other.propertyRenames
        )

    companion object {
        val Default by lazy { CorrectionConfiguration.Default.nonClassMemberCorrections }
        val Empty by lazy { CorrectionConfiguration.Empty.nonClassMemberCorrections }
    }
}

data class StandardCorrectionsConfiguration(
    val commonPrefixReplacements: Map<String, String> = emptyMap(),
    val ignoreFunctions: List<String> = emptyList(),
    val ignoreProperties: List<String> = emptyList(),
    val allMemberFunctions: List<FunctionCorrectionConfiguration> = emptyList(),
    val allMemberProperties: List<PropertyCorrectionConfiguration> = emptyList(),
    val allMembers: List<MemberCorrectionConfiguration> = emptyList(),
    val classes: List<ClassCorrectionConfiguration> = emptyList(),
) : ClassConfigurationsProvider {
    override fun classConfigurations(): List<ClassCorrectionConfiguration> = classes()

    fun commonPrefixReplacements() = if (this == Default) commonPrefixReplacements
    else Default.commonPrefixReplacements + commonPrefixReplacements

    fun ignoreFunctionNames() = if (this == Default) ignoreFunctions
    else Default.ignoreFunctions + ignoreFunctions

    fun ignorePropertyNames() = if (this == Default) ignoreProperties
    else Default.ignoreProperties + ignoreProperties

    fun allMemberFunctions() = if (this == Default) allMemberFunctions
    else Default.allMemberFunctions + allMemberFunctions

    fun allMemberProperties() = if (this == Default) allMemberProperties
    else Default.allMemberProperties + allMemberProperties

    fun allMembers() = if (this == Default) allMembers
    else Default.allMembers + allMembers

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

    operator fun plus(other: StandardCorrectionsConfiguration) =
        StandardCorrectionsConfiguration(
            commonPrefixReplacements + other.commonPrefixReplacements,
            ignoreFunctions + other.ignoreFunctions,
            ignoreProperties + other.ignoreProperties,
            allMemberFunctions + other.allMemberFunctions,
            allMemberProperties + other.allMemberProperties,
            allMembers + other.allMembers,
            classes + other.classes
        )

    companion object {
        val Default by lazy { CorrectionConfiguration.Default.standardCorrections }
        val Empty by lazy { CorrectionConfiguration.Empty.standardCorrections }
    }
}

interface ClassConfigurationsProvider {
    fun classConfigurations(): List<ClassCorrectionConfiguration>

    fun definedClassRenames() = classConfigurations()
        .filter { it.newName != null }
        .associate { it.name to it.newName!! }

    fun definedMemberRenames(): Map<String, Map<String, String>> = classConfigurations()
        .filter { it.members.isNotEmpty() && it.members.any { member -> member.newName != null } }
        .associate {
            it.name to it.members
                .associate { member -> member.name to member.newName!! }
        }

    operator fun get(className: String): ClassCorrectionConfiguration? =
        classConfigurations().find { it.name == className }

}

data class UnnamedClassesConfiguration(
    val classes: List<ClassCorrectionConfiguration> = emptyList()
) : ClassConfigurationsProvider {
    override fun classConfigurations(): List<ClassCorrectionConfiguration> = if (this == Default) classes
    else Default.classes + classes

    operator fun plus(other: UnnamedClassesConfiguration) =
        UnnamedClassesConfiguration(classes + other.classes)

    companion object {
        val Default by lazy { CorrectionConfiguration.Default.unnamedClasses }
        val Empty by lazy { CorrectionConfiguration.Empty.unnamedClasses }
    }
}

data class ClassCorrectionConfiguration(
    val name: String = "",
    val newName: String? = null,
    val superType: String? = null,
    val delete: Boolean = false,
    val removeSuperTypes: List<String> = emptyList(),
    val addSuperTypes: List<String> = emptyList(),
    val members: List<MemberCorrectionConfiguration> = emptyList(),
    val functions: List<FunctionCorrectionConfiguration> = emptyList(),
    val properties: List<PropertyCorrectionConfiguration> = emptyList(),


    ) {
    fun member(name: String): MemberCorrectionConfiguration? =
        members.find { it.name == name } ?: functions.find { it.name == name } ?: properties.find { it.name == name }

    fun function(name: String): FunctionCorrectionConfiguration? = functions.find { it.name == name }
    fun property(name: String): PropertyCorrectionConfiguration? = properties.find { it.name == name }

    private fun ClassDeclaration.isTargetName(): Boolean = originalName == name || originalName == newName
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

        val declarationFunctions = declaration.functions
        val declarationProperties = declaration.properties

        functions.forEach { function ->
            val declarationFunction = declarationFunctions.find { it.originalName == function.name }
            if (declarationFunction != null) function.correct(declarationFunction)
        }

        properties.forEach { property ->
            val declarationProperty = declarationProperties.find { it.originalName == property.name }
            if (declarationProperty != null) property.correct(declarationProperty)
        }

        members.forEach { member ->
            val declarationFunction = declarationFunctions.find { it.originalName == member.name }
            if (declarationFunction != null) member.correct(declarationFunction)
            val declarationProperty = declarationProperties.find { it.originalName == member.name }
            if (declarationProperty != null) member.correct(declarationProperty)
        }


    }

}

open class MemberCorrectionConfiguration(
    open val name: String = "",
    open val newName: String? = null
) {

    open fun correct(declaration: DeclarationWithName) {
        if (declaration.originalName == name) {
            if (newName != null) {
                declaration.rename("memberConfiguration", newName!!)
                declaration.lockRenaming()
            }
        }
    }

}

data class FunctionCorrectionConfiguration(
    override val name: String = "",
    override val newName: String? = null,
    val returnType: String? = null,
    val newReturnType: String? = null,
    val shouldOverride: Boolean? = null,
    val removeTypeVariables: Boolean? = null,
    val renameParameters: Map<String, String> = emptyMap(),
    val removeParameters: List<String> = emptyList(),
    val addParameters: Map<String, String> = emptyMap(),
) : MemberCorrectionConfiguration(name, newName) {

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
}

data class PropertyCorrectionConfiguration(
    override val name: String = "",
    override val newName: String? = null,
    val type: String? = null,
    val newType: String? = null,
    val shouldOverride: Boolean? = null
) : MemberCorrectionConfiguration(name, newName) {

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
}
