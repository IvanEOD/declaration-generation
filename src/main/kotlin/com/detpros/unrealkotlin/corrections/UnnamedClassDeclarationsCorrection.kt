package com.detpros.unrealkotlin.corrections


import com.detpros.unrealkotlin.utility.buildAll
import com.detpros.unrealkotlin.utility.builders
import com.detpros.unrealkotlin.corrections.models.ClassCorrectionsProvider
import com.detpros.unrealkotlin.corrections.models.ClassCorrection
import com.detpros.unrealkotlin.corrections.models.CorrectionWithClasses

/**
 *  Unnamed Class Declarations Correction
 *
 * @author IvanEOD ( 5/26/2023 at 3:15 PM EST )
 */
data class UnnamedClassDeclarationsCorrection(
    override val classes: List<ClassCorrection> = emptyList()
) : ClassCorrectionsProvider,
    CorrectionWithClasses<UnnamedClassDeclarationsCorrection, UnnamedClassDeclarationsCorrection.Builder> {
    override fun classConfigurations(): List<ClassCorrection> = if (this == Default) classes
    else Default.classes + classes

    operator fun plus(other: UnnamedClassDeclarationsCorrection) =
        UnnamedClassDeclarationsCorrection(classes + other.classes)

    override fun toBuilder() = Builder(classes.builders())


    class Builder(
        classes: List<ClassCorrection.Builder> = emptyList()
    ) : CorrectionWithClasses.Builder<UnnamedClassDeclarationsCorrection, Builder> {
        private val classes = classes.toMutableList()
        override fun classConfiguration(configuration: ClassCorrection.Builder) = apply {
            classes.add(configuration)
        }

        override fun include(other: UnnamedClassDeclarationsCorrection) = apply {
            classes.addAll(other.classes.builders())
        }

        override fun include(other: Builder) = apply {
            classes.addAll(other.classes)
        }

        override fun build(): UnnamedClassDeclarationsCorrection = UnnamedClassDeclarationsCorrection(classes.buildAll())
    }

    companion object {
        val Default by lazy { UnrealDeclarationsCorrection.Default.unnamedClasses }
        val Empty by lazy { UnrealDeclarationsCorrection.Empty.unnamedClasses }
    }
}
