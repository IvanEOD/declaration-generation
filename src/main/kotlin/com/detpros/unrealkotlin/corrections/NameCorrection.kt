package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.declaration.DeclarationWithName


/**
 *  Name Correction
 *
 * @author IvanEOD ( 6/9/2023 at 1:18 PM EST )
 */
sealed interface NameCorrection : GeneralCorrection {

    fun ClassCorrectionContext.getName(): String
    fun FunctionCorrectionContext.getName(): String
    fun PropertyCorrectionContext.getName(): String
    fun ParameterCorrectionContext.getName(): String
    fun TypeAliasCorrectionContext.getName(): String

    private fun rename(declaration: DeclarationWithName, newName: String) {
        if (declaration.name != newName) {
            declaration.rename("Name Correction", newName)
            declaration.lockRenaming()
        }
    }

    override fun correct(context: ClassCorrectionContext) = rename(context.declaration, context.getName())
    override fun correct(context: FunctionCorrectionContext) = rename(context.declaration, context.getName())
    override fun correct(context: PropertyCorrectionContext) = rename(context.declaration, context.getName())
    override fun correct(context: ParameterCorrectionContext) = rename(context.declaration, context.getName())
    override fun correct(context: TypeAliasCorrectionContext) = rename(context.declaration, context.getName())

    companion object {
        val NoOp: NameCorrection = Correction.NoOp
    }

}

data class SetName(
    val name: String
): NameCorrection {
    override fun ClassCorrectionContext.getName(): String = name
    override fun FunctionCorrectionContext.getName(): String = name
    override fun PropertyCorrectionContext.getName(): String = name
    override fun ParameterCorrectionContext.getName(): String = name
    override fun TypeAliasCorrectionContext.getName(): String = name
}

data class SetNameConditionally(val name: String, val condition: (DeclarationWithName) -> Boolean): NameCorrection by SetName(name) {
    override fun correct(context: ClassCorrectionContext) {
        if (condition(context.declaration)) super.correct(context)
    }

    override fun correct(context: FunctionCorrectionContext) {
        if (condition(context.declaration)) super.correct(context)
    }

    override fun correct(context: PropertyCorrectionContext) {
        if (condition(context.declaration)) super.correct(context)
    }

    override fun correct(context: ParameterCorrectionContext) {
        if (condition(context.declaration)) super.correct(context)
    }

    override fun correct(context: TypeAliasCorrectionContext) {
        if (condition(context.declaration)) super.correct(context)
    }
}


//class SetMemberName private constructor(
//    val name: String,
//    private val filter: (CorrectionContext) -> Boolean
//) : ClassCorrection, FunctionCorrection {
//
//    constructor(name: String): this(name, { _: CorrectionContext -> true })
//    constructor(
//        name: String,
//        filter: (DeclarationWithName) -> Boolean
//    ): this(name, { ctx: CorrectionContext -> ctx.declaration.let { it is DeclarationWithName && filter(it) }  })
//
//
//    private val correction by lazy { SetNameConditionally(name) {  filter(it.declaration) } }
//
//    override val defaultSubclassCorrection: ClassCorrection get() = correction
//    override val defaultPropertyCorrection: PropertyCorrection get() = correction
//    override val defaultFunctionCorrection: FunctionCorrection get() = correction
//    override val defaultParameterCorrection: ParameterCorrection get() = correction
//
//    override fun correct(context: ClassCorrectionContext) {
//        context.onMembers(correction, filter)
//    }
//
//    override fun correct(context: FunctionCorrectionContext) {
//
//    }
//}
