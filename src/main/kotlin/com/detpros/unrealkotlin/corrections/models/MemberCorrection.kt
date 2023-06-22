package com.detpros.unrealkotlin.corrections.models

/**
 *  Member Correction
 *
 * @author IvanEOD ( 6/22/2023 at 12:08 PM EST )
 */
open class MemberCorrection(
    override val name: String = "",
    override val newName: String? = null
) : BaseMemberCorrection<MemberCorrection, MemberCorrection.Builder>() {

    override fun toBuilder() = Builder(name, newName)

    class Builder(
        override val name: String,
        override var newName: String? = null
    ): BaseMemberCorrection.Builder<MemberCorrection, Builder>() {
        override fun include(other: MemberCorrection) = apply {
            if (other.name == name && other.newName != null) newName = other.newName
        }
        override fun include(other: Builder) = apply {
            if (other.name == name && other.newName != null) newName = other.newName
        }
        override fun build(): MemberCorrection = MemberCorrection(name, newName)
    }

}