package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.declaration.*


/**
 *  Correction
 *
 * @author IvanEOD ( 6/9/2023 at 11:54 AM EST )
 */
sealed class CorrectionContext {

    abstract val declaration: Declaration
    abstract val parentContext: CorrectionContext?

    private var isNameCorrectionLocked: Boolean = false

    private var _nameCorrection: NameCorrection = NameCorrection.NoOp
        set(value) {
            if (!isNameCorrectionLocked) field = value
        }


    val nameCorrection: NameCorrection get() = _nameCorrection

    fun lockNameCorrection() {
        isNameCorrectionLocked = true
    }

    private var _completed = false
    val isComplete: Boolean get() = _completed


    fun useNameCorrection(correction: NameCorrection, lock: Boolean = true) {
        _nameCorrection = correction
        if (lock) lockNameCorrection()
    }



    protected fun complete() {
        _completed = true
    }

    abstract fun findMember(name: String, type: DeclarationType): CorrectionContext?
    fun hasMember(name: String, type: DeclarationType): Boolean = findMember(name, type) != null


}

class ClassCorrectionContext(
    override val declaration: ClassDeclaration,
    override val parentContext: ClassCorrectionContext? = null,
): CorrectionContext() {
    val functions by lazy { declaration.functions.map(::FunctionCorrectionContext) }
    val properties by lazy { declaration.properties.map(::PropertyCorrectionContext) }
    val classes by lazy { declaration.classes.map(::ClassCorrectionContext) }

    fun onMembers(correction: Correction) {
        if (correction is FunctionCorrection) functions.forEach(correction::correct)
        if (correction is PropertyCorrection) properties.forEach(correction::correct)
        if (correction is ClassCorrection) classes.forEach(correction::correct)
    }

    fun onProperties(correction: PropertyCorrection) {
        properties.forEach(correction::correct)
    }

    fun onFunctions(correction: FunctionCorrection) {
        functions.forEach(correction::correct)
    }

    @JvmName("onMembersDeclaration")
    fun onMembers(correction: Correction, filter: (DeclarationWithName) -> Boolean) {
        if (correction is FunctionCorrection) functions.filter { filter(it.declaration) }.forEach(correction::correct)
        if (correction is PropertyCorrection) properties.filter { filter(it.declaration) }.forEach(correction::correct)
        if (correction is ClassCorrection) classes.filter { filter(it.declaration) }.forEach(correction::correct)
    }

    fun onMembers(correction: Correction, filter: (CorrectionContext) -> Boolean) {
        if (correction is FunctionCorrection) functions.filter { filter(it) }.forEach(correction::correct)
        if (correction is PropertyCorrection) properties.filter { filter(it) }.forEach(correction::correct)
        if (correction is ClassCorrection) classes.filter { filter(it) }.forEach(correction::correct)
    }

    fun onMember(name: String, correction: Correction) = when (correction) {
        is FunctionCorrection -> findFunction(name)?.let(correction::correct)
        is PropertyCorrection -> findProperty(name)?.let(correction::correct)
        is ClassCorrection -> findClass(name)?.let(correction::correct)
    }
    @JvmName("onPropertiesDeclaration")
    fun onProperties(correction: PropertyCorrection, filter: (PropertyDeclaration) -> Boolean) {
        properties.filter { filter(it.declaration) } .forEach(correction::correct)
    }

    fun onProperties(correction: PropertyCorrection, filter: (PropertyCorrectionContext) -> Boolean) {
        properties.filter { filter(it) } .forEach(correction::correct)
    }

    fun onProperty(name: String, correction: PropertyCorrection) = findProperty(name)?.let(correction::correct)

    @JvmName("onFunctionsDeclaration")
    fun onFunctions(correction: FunctionCorrection, filter: (FunctionDeclaration) -> Boolean) {
        functions.filter { filter(it.declaration) } .forEach(correction::correct)
    }

    fun onFunctions(correction: FunctionCorrection, filter: (FunctionCorrectionContext) -> Boolean) {
        functions.filter { filter(it) } .forEach(correction::correct)
    }

    fun onFunction(name: String, correction: FunctionCorrection) = findFunction(name)?.let(correction::correct)

    @JvmName("onClassesDeclaration")
    fun onClasses(correction: ClassCorrection, filter: (ClassDeclaration) -> Boolean) {
        classes.filter { filter(it.declaration) } .forEach(correction::correct)
    }

    fun onClasses(correction: ClassCorrection, filter: (ClassCorrectionContext) -> Boolean) {
        classes.filter { filter(it) } .forEach(correction::correct)
    }

    fun onClass(name: String, correction: ClassCorrection) = findClass(name)?.let(correction::correct)


    fun findFunction(name: String): FunctionCorrectionContext? = functions.find { it.declaration.name == name }
    fun findProperty(name: String): PropertyCorrectionContext? = properties.find { it.declaration.name == name }
    fun findClass(name: String): ClassCorrectionContext? = classes.find { it.declaration.name == name }
    fun findMember(name: String): CorrectionContext? = findFunction(name) ?: findProperty(name) ?: findClass(name)

    override fun findMember(name: String, type: DeclarationType): CorrectionContext? =
        when (type) {
            DeclarationType.Function -> findFunction(name)
            DeclarationType.Property -> findProperty(name)
            DeclarationType.Class -> findClass(name)
            else -> null
        }



}

class FunctionCorrectionContext(
    override val declaration: FunctionDeclaration,
    override val parentContext: ClassCorrectionContext? = null,
): CorrectionContext() {
    val parameters by lazy { declaration.parameters.map(::ParameterCorrectionContext) }

    fun findParameter(name: String): ParameterCorrectionContext? =
        parameters.find { it.declaration.name == name }

    fun findMember(name: String): CorrectionContext? =
        findParameter(name)

    fun onParameters(correction: ParameterCorrection) {
        parameters.forEach(correction::correct)
    }

    fun onParameter(name: String, correction: ParameterCorrection) {
        findParameter(name)?.let(correction::correct)
    }

    override fun findMember(name: String, type: DeclarationType): CorrectionContext? =
        when (type) {
            DeclarationType.Parameter -> findParameter(name)
            else -> null
        }


}

class PropertyCorrectionContext(
    override val declaration: PropertyDeclaration,
    override val parentContext: ClassCorrectionContext? = null,
): CorrectionContext() {
    override fun findMember(name: String, type: DeclarationType): CorrectionContext? = null

}

class ParameterCorrectionContext(
    override val declaration: ParameterDeclaration,
    override val parentContext: FunctionCorrectionContext? = null,
): CorrectionContext() {
    override fun findMember(name: String, type: DeclarationType): CorrectionContext? = null

}

class TypeAliasCorrectionContext(
    override val declaration: TypeAliasDeclaration
): CorrectionContext() {
    override val parentContext: CorrectionContext? = null
    override fun findMember(name: String, type: DeclarationType): CorrectionContext? = null
}

