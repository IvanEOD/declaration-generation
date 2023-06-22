package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.utility.buildAll
import com.detpros.unrealkotlin.utility.builders
import com.detpros.unrealkotlin.corrections.models.ClassCorrectionsProvider
import com.detpros.unrealkotlin.corrections.models.ClassCorrection
import com.detpros.unrealkotlin.corrections.models.CorrectionWithClasses


/**
 *  Enum Declarations Correction
 *
 * @author IvanEOD ( 6/22/2023 at 1:31 PM EST )
 */
data class EnumDeclarationsCorrection(
    override val classes: List<ClassCorrection> = emptyList(),
) : ClassCorrectionsProvider,
    CorrectionWithClasses<EnumDeclarationsCorrection, EnumDeclarationsCorrection.Builder> {

    override fun classConfigurations(): List<ClassCorrection> = if (this == Default) classes
    else Default.classes + classes

    operator fun plus(other: EnumDeclarationsCorrection) =
        EnumDeclarationsCorrection(classes + other.classes)

    override fun toBuilder() = Builder(classes.builders())

    class Builder(
        classes: List<ClassCorrection.Builder> = emptyList()
    ) : CorrectionWithClasses.Builder<EnumDeclarationsCorrection, Builder> {
        private val classes = classes.toMutableList()

        override fun classConfiguration(configuration: ClassCorrection.Builder): Builder {
            classes.add(configuration)
            return this
        }

        fun named(name: String, block: ClassCorrection.Builder.() -> Unit) = apply {
            classes.add(ClassCorrection.Builder(name).apply(block))
        }
        fun rename(name: String, newName: String) = named(name) { rename(newName) }

        override fun include(other: EnumDeclarationsCorrection) = apply {
            classes.addAll(other.classes.builders())
        }
        override fun include(other: Builder) = apply {
            classes.addAll(other.classes)
        }

        override fun build(): EnumDeclarationsCorrection = EnumDeclarationsCorrection(classes.buildAll())
    }

    companion object {
        val Default by lazy { UnrealDeclarationsCorrection.Default.enumCorrections }
        val Empty by lazy { UnrealDeclarationsCorrection.Empty.enumCorrections }
    }
}



