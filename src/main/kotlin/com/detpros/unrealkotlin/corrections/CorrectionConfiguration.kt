package com.detpros.unrealkotlin.corrections

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
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
): ClassConfigurationsProvider {

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
): ClassConfigurationsProvider {
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
): ClassConfigurationsProvider {
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

interface ClassConfigurationsProvider  {
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


}

data class UnnamedClassesConfiguration(
    val classes: List<ClassCorrectionConfiguration> = emptyList()
): ClassConfigurationsProvider {
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
)

open class MemberCorrectionConfiguration(
    open val name: String = "",
    open val newName: String? = null
)

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
) : MemberCorrectionConfiguration(name, newName)

data class PropertyCorrectionConfiguration(
    override val name: String = "",
    override val newName: String? = null,
    val type: String? = null,
    val newType: String? = null,
    val shouldOverride: Boolean? = null
) : MemberCorrectionConfiguration(name, newName)