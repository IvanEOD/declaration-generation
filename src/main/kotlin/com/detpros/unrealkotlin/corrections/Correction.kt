package com.detpros.unrealkotlin.corrections

/**
 *  Correction
 *
 * @author IvanEOD ( 6/9/2023 at 12:19 PM EST )
 */
sealed interface Correction {



    object NoOp : GeneralCorrection, NameCorrection {
        override val defaultSubclassCorrection: ClassCorrection get() = this
        override val defaultPropertyCorrection: PropertyCorrection get() = this
        override val defaultFunctionCorrection: FunctionCorrection get() = this
        override val defaultParameterCorrection: ParameterCorrection get() = this

        override fun correct(context: ClassCorrectionContext) {}
        override fun correct(context: FunctionCorrectionContext) {}
        override fun correct(context: PropertyCorrectionContext) {}
        override fun correct(context: ParameterCorrectionContext) {}
        override fun correct(context: TypeAliasCorrectionContext) {}



        override fun ClassCorrectionContext.getName(): String = declaration.name
        override fun FunctionCorrectionContext.getName(): String = declaration.name
        override fun PropertyCorrectionContext.getName(): String = declaration.name
        override fun ParameterCorrectionContext.getName(): String = declaration.name
        override fun TypeAliasCorrectionContext.getName(): String = declaration.name

    }




}

sealed interface GeneralCorrection : ClassCorrection, FunctionCorrection, PropertyCorrection, ParameterCorrection, TypeAliasCorrection {
    override val defaultPropertyCorrection: PropertyCorrection get() = this
    override val defaultFunctionCorrection: FunctionCorrection get() = this
}

open class ConditionalCorrection<T, C : GeneralCorrection>(
    private val condition: (T) -> Boolean,
    private val correction: C
): GeneralCorrection by correction



sealed interface ClassCorrection : Correction {

    val defaultSubclassCorrection : ClassCorrection get() = NoOp
    val defaultPropertyCorrection: PropertyCorrection get() = PropertyCorrection.NoOp
    val defaultFunctionCorrection: FunctionCorrection get() = FunctionCorrection.NoOp

    fun correct(context: ClassCorrectionContext)

    companion object {
        val NoOp: ClassCorrection = Correction.NoOp
    }

}

sealed interface FunctionCorrection : Correction {

    val defaultParameterCorrection: ParameterCorrection get() = ParameterCorrection.NoOp

    fun correct(context: FunctionCorrectionContext)

    companion object {
        val NoOp: FunctionCorrection = Correction.NoOp
    }

}

sealed interface PropertyCorrection : Correction {
    fun correct(context: PropertyCorrectionContext)

    companion object {
        val NoOp: PropertyCorrection = Correction.NoOp
    }

}

sealed interface ParameterCorrection : Correction {
    fun correct(context: ParameterCorrectionContext)

    companion object {
        val NoOp: ParameterCorrection = Correction.NoOp
    }

}

sealed interface TypeAliasCorrection : Correction {
    fun correct(context: TypeAliasCorrectionContext)

    companion object {
        val NoOp: TypeAliasCorrection = Correction.NoOp
    }

}
