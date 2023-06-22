package com.detpros.unrealkotlin.corrections.models


/**
 *  Correction With Classes
 *
 * @author IvanEOD ( 6/22/2023 at 12:59 PM EST )
 */
interface CorrectionWithClasses<T : CorrectionWithClasses<T, B>, B : CorrectionWithClasses.Builder<T, B>> : Correction<T, B> {

    val classes: List<ClassCorrection>

    interface Builder<T: CorrectionWithClasses<T, B>, B : Builder<T, B>>: Correction.Builder<T, B> {

        fun classConfiguration(configuration: ClassCorrection.Builder): B

        fun klass(name: String, block: ClassCorrection.Builder.() -> Unit) =
            classConfiguration(ClassCorrection.Builder(name).apply(block))

        fun klassWithSupertype(type: String, block: ClassCorrection.Builder.() -> Unit) =
            classConfiguration(ClassCorrection.Builder("", superType = type).apply(block))

        fun renameClass(name: String, newName: String) = klass(name) { rename(newName) }

    }

}