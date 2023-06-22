package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.corrections.models.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.File
import java.net.URL


/**
 *  Unreal Declarations Correction
 *
 * @author IvanEOD ( 6/5/2023 at 10:04 AM EST )
 */

data class UnrealDeclarationsCorrection(
    val enumCorrections: EnumDeclarationsCorrection = EnumDeclarationsCorrection(),
    val nonClassMemberCorrections: NonClassMemberDeclarationsCorrection = NonClassMemberDeclarationsCorrection(),
    val standardCorrections: StandardDeclarationsCorrection = StandardDeclarationsCorrection(),
    val unnamedClasses: UnnamedClassDeclarationsCorrection = UnnamedClassDeclarationsCorrection()
) : ClassCorrectionsProvider,
    Correction<UnrealDeclarationsCorrection, UnrealDeclarationsCorrection.Builder> {

    override fun classConfigurations(): List<ClassCorrection> =
        enumCorrections.classConfigurations() +
                unnamedClasses.classConfigurations()

    operator fun plus(other: UnrealDeclarationsCorrection) =
        UnrealDeclarationsCorrection(
            enumCorrections + other.enumCorrections,
            nonClassMemberCorrections + other.nonClassMemberCorrections,
            standardCorrections + other.standardCorrections,
            unnamedClasses + other.unnamedClasses
        )

    override fun toBuilder() = Builder(
        enumCorrections.toBuilder(),
        nonClassMemberCorrections.toBuilder(),
        standardCorrections.toBuilder(),
        unnamedClasses.toBuilder()
    )

    class Builder(
        private val enumsBuilder: EnumDeclarationsCorrection.Builder = EnumDeclarationsCorrection.Builder(),
        private val nonClassMembersBuilder: NonClassMemberDeclarationsCorrection.Builder = NonClassMemberDeclarationsCorrection.Builder(),
        private val standardBuilder: StandardDeclarationsCorrection.Builder = StandardDeclarationsCorrection.Builder(),
        private val unnamedClassesBuilder: UnnamedClassDeclarationsCorrection.Builder = UnnamedClassDeclarationsCorrection.Builder(),
    ) : Correction.Builder<UnrealDeclarationsCorrection, Builder> {

        override fun include(other: UnrealDeclarationsCorrection) = apply {
            enumsBuilder.include(other.enumCorrections.toBuilder())
            nonClassMembersBuilder.include(other.nonClassMemberCorrections.toBuilder())
            standardBuilder.include(other.standardCorrections.toBuilder())
            unnamedClassesBuilder.include(other.unnamedClasses.toBuilder())
        }

        override fun include(other: Builder) = apply {
            enumsBuilder.include(other.enumsBuilder)
            nonClassMembersBuilder.include(other.nonClassMembersBuilder)
            standardBuilder.include(other.standardBuilder)
            unnamedClassesBuilder.include(other.unnamedClassesBuilder)
        }

        fun enums(block: EnumDeclarationsCorrection.Builder.() -> Unit) = apply {
            enumsBuilder.block()
        }

        fun nonClassMembers(block: NonClassMemberDeclarationsCorrection.Builder.() -> Unit) = apply {
            nonClassMembersBuilder.block()
        }

        fun standard(block: StandardDeclarationsCorrection.Builder.() -> Unit) = apply {
            standardBuilder.block()
        }

        fun unnamedClasses(block: UnnamedClassDeclarationsCorrection.Builder.() -> Unit) = apply {
            unnamedClassesBuilder.block()
        }

        override fun build(): UnrealDeclarationsCorrection = UnrealDeclarationsCorrection(
            enumsBuilder.build(),
            nonClassMembersBuilder.build(),
            standardBuilder.build(),
            unnamedClassesBuilder.build()
        )
    }


    companion object {

        private fun String.toFile() = File(this)

        private inline fun <reified T> String.parseYaml(): T {
            val objectMapper = ObjectMapper(YAMLFactory())
            return objectMapper.readValue(this, T::class.java)
        }

        fun loadFromYaml(yaml: String): UnrealDeclarationsCorrection = yaml.parseYaml()
        fun loadFromFile(file: File): UnrealDeclarationsCorrection = file.readText().parseYaml()
        fun loadFromFile(path: String): UnrealDeclarationsCorrection = loadFromFile(path.toFile())
        fun File.loadCorrectionConfiguration(): UnrealDeclarationsCorrection = loadFromFile(this)


        val Default: UnrealDeclarationsCorrection by lazy {
            downloadTextFromUrl(DefaultConfigUrl).parseYaml()
        }

        val Empty: UnrealDeclarationsCorrection by lazy {
            downloadTextFromUrl(EmptyConfigUrl).parseYaml()
        }

        private fun downloadTextFromUrl(url: String): String = URL(url).readText()

        private const val DefaultConfigUrl =
            "https://raw.githubusercontent.com/IvanEOD/declaration-generation/main/src/main/resources/defaultUnrealKtConfiguration.yml"
        private const val EmptyConfigUrl =
            "https://raw.githubusercontent.com/IvanEOD/declaration-generation/main/src/main/resources/emptyUnrealKtConfiguration.yml"
    }

}






