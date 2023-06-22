package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.corrections.models.Correction


/**
 *  Non Class Member Declarations Correction
 *
 * @author IvanEOD ( 6/22/2023 at 1:23 PM EST )
 */
data class NonClassMemberDeclarationsCorrection(
    val typeAliasRenames: Map<String, String> = emptyMap(),
    val propertyRenames: Map<String, String> = emptyMap()
) : Correction<NonClassMemberDeclarationsCorrection, NonClassMemberDeclarationsCorrection.Builder> {

    fun typeAliasRenames() = if (this == Default) typeAliasRenames
    else Default.typeAliasRenames + typeAliasRenames

    fun propertyRenames() = if (this == Default) propertyRenames
    else Default.propertyRenames + propertyRenames

    operator fun plus(other: NonClassMemberDeclarationsCorrection) =
        NonClassMemberDeclarationsCorrection(
            typeAliasRenames + other.typeAliasRenames,
            propertyRenames + other.propertyRenames
        )

    override fun toBuilder() = Builder(typeAliasRenames, propertyRenames)

    class Builder(
        typeAliasRenames: Map<String, String> = emptyMap(),
        propertyRenames: Map<String, String> = emptyMap(),
    ) : Correction.Builder<NonClassMemberDeclarationsCorrection, Builder> {
        private val typeAliasRenames = typeAliasRenames.toMutableMap()
        private val propertyRenames = propertyRenames.toMutableMap()

        fun renameTypeAlias(typeAlias: String, newName: String) = apply {
            typeAliasRenames[typeAlias] = newName
        }

        fun renameProperty(property: String, newName: String) = apply {
            propertyRenames[property] = newName
        }

        override fun include(other: NonClassMemberDeclarationsCorrection) = apply {
            typeAliasRenames.putAll(other.typeAliasRenames)
            propertyRenames.putAll(other.propertyRenames)
        }
        override fun include(other: Builder) = apply {
            typeAliasRenames.putAll(other.typeAliasRenames)
            propertyRenames.putAll(other.propertyRenames)
        }

        override fun build(): NonClassMemberDeclarationsCorrection = NonClassMemberDeclarationsCorrection(
            typeAliasRenames,
            propertyRenames
        )

    }


    companion object {
        val Default by lazy { UnrealDeclarationsCorrection.Default.nonClassMemberCorrections }
        val Empty by lazy { UnrealDeclarationsCorrection.Empty.nonClassMemberCorrections }
    }
}