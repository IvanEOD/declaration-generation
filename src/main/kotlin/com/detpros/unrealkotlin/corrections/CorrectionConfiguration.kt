package com.detpros.unrealkotlin.corrections

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.File


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
) {

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

//        val Default: CorrectionConfiguration by lazy {
//
//        }
    }

}

data class EnumCorrectionsConfiguration(
    val classes: List<ClassCorrectionConfiguration> = emptyList(),
)

data class NonClassMemberCorrectionsConfiguration(
    val typeAliasRenames: Map<String, String> = emptyMap(),
    val propertyRenames: Map<String, String> = emptyMap()
)

data class StandardCorrectionsConfiguration(
    val commonPrefixReplacements: Map<String, String> = emptyMap(),
    val ignoreFunctions: List<String> = emptyList(),
    val ignoreProperties: List<String> = emptyList(),
    val allMemberFunctions: List<FunctionCorrectionConfiguration> = emptyList(),
    val allMemberProperties: List<PropertyCorrectionConfiguration> = emptyList(),
    val allMembers: List<MemberCorrectionConfiguration> = emptyList(),
    val classes: List<ClassCorrectionConfiguration> = emptyList(),
)

data class UnnamedClassesConfiguration(
    val classes: List<ClassCorrectionConfiguration> = emptyList()
)

data class ClassCorrectionConfiguration(
    val name: String = "",
    val newName: String? = null,
    val superType: String? = null,
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
): MemberCorrectionConfiguration(name, newName)

data class PropertyCorrectionConfiguration(
    override val name: String = "",
    override val newName: String? = null,
    val type: String? = null,
    val newType: String? = null,
    val shouldOverride: Boolean? = null
): MemberCorrectionConfiguration(name, newName)