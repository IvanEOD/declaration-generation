package com.detpros.unrealkotlin.declaration

import com.detpros.unrealkotlin.utility.addFolding
import com.detpros.unrealkotlin.utility.addIfNotEmpty
import com.detpros.unrealkotlin.utility.addIfNotNull
import com.detpros.unrealkotlin.utility.isSameAs
import com.squareup.kotlinpoet.*


/**
 *  Function Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 11:00 AM EST )
 */

internal class FunctionDeclarationImpl(
    name: String,
    receiverType: TypeNameDeclaration?,
    returnType: TypeNameDeclaration?,
    typeVariables: Collection<TypeVariableNameDeclaration>,
    parameters: Collection<ParameterDeclaration>,
    delegateConstructor: String?,
    delegateConstructorArguments: Collection<CodeDeclaration>,
    annotations: Collection<AnnotationDeclaration>,
    modifiers: Collection<KModifier>,
): BaseJsNameDeclaration(), FunctionDeclaration, ChildDeclaration {

    private var _receiverType = receiverType
    private var _returnType = returnType
    private val _typeVariables = typeVariables.toMutableSet()
    private val _parameters = parameters.toMutableSet()
    private var _delegateConstructor = delegateConstructor
    private val _delegateConstructorArguments = delegateConstructorArguments.toMutableSet()
    private val _modifiers = modifiers.toMutableSet()
    init {
        _name = name
        _annotations.addAll(annotations)
    }
    override var _parent: ParentDeclaration? = null
    override val modifiers: Set<KModifier> get() = _modifiers
    override val originalName: String = name
    override val name: String get() = _name
    override val receiverType: TypeNameDeclaration? get() = _receiverType
    override val returnType: TypeNameDeclaration? get() = _returnType
    override val typeVariables: Set<TypeVariableNameDeclaration> get() = _typeVariables
    override val parameters: Set<ParameterDeclaration> get() = _parameters
    override val delegateConstructor: String? get() = _delegateConstructor
    override val delegateConstructorArguments: Set<CodeDeclaration> get() = _delegateConstructorArguments
    override val annotations: Set<AnnotationDeclaration> get() = _annotations

    override val members: Set<Declaration> get() =
        setOfNotNull(receiverType, returnType) + annotations + typeVariables + parameters + delegateConstructorArguments

    override val isJsNameAllowed: Boolean
        get() = !isOverride && name != "constructor()" && name != "constructor"

    override fun addAnnotation(annotation: AnnotationDeclaration) {
        _annotations.add(annotation)
    }

    override fun removeAnnotation(annotation: AnnotationDeclaration) {
        _annotations.remove(annotation)
    }

    override fun clearAnnotations() {
        _annotations.clear()
    }

    override fun onChildClaimed(declaration: ChildDeclaration) {

    }

    override fun onChildReleased(declaration: ChildDeclaration) {

    }



    override fun setModifiers(modifiers: Collection<KModifier>) {
        _modifiers.clear()
        _modifiers.addAll(modifiers)
    }

    override fun addModifier(modifier: KModifier) {
        _modifiers.add(modifier)
    }

    override fun removeModifier(modifier: KModifier) {
        _modifiers.remove(modifier)
    }
    override fun removeTypeVariables() {
        _parameters.forEach { parameter ->
            if (parameter.isVariableType()) parameter.setTypeVariableToBound()
        }
        _typeVariables.clear()
    }

    override fun hasTypeVariables(): Boolean = typeVariables.isNotEmpty()

    override fun changeParameterType(name: String, type: TypeNameDeclaration) {
        val parameter = parameters.find { it.name == name } ?: return
        parameter.changeType(type)
    }

    override fun changeParameterType(name: String, type: String) {
        val parameter = parameters.find { it.name == name } ?: return
        parameter.changeType(type)
    }



    override fun isSameAs(other: FunctionDeclaration): Boolean =
        name == other.name &&
        returnType == other.returnType &&
        receiverType == other.receiverType &&
        typeVariables.isSameAs(other.typeVariables, TypeVariableNameDeclaration::isSameAs) &&
        parameters.isSameAs(other.parameters, ParameterDeclaration::isSameAs) &&
        delegateConstructor == other.delegateConstructor &&
        delegateConstructorArguments.isSameAs(other.delegateConstructorArguments, CodeDeclaration::isSameAs)

    override fun isSameAs(other: FunSpec): Boolean =
        name == other.name &&
        returnType == other.returnType &&
        receiverType == other.receiverType &&
        typeVariables.isSameAs(other.typeVariables, TypeVariableNameDeclaration::isSameAs) &&
        parameters.isSameAs(other.parameters, ParameterDeclaration::isSameAs) &&
        delegateConstructor == other.delegateConstructor &&
        delegateConstructorArguments.isSameAs(other.delegateConstructorArguments, CodeDeclaration::isSameAs)


    private var _funSpec: FunSpec? = null

    override fun toFunSpec(): FunSpec {
        if (_funSpec == null) _funSpec = FunSpec.builder(_name)
            .addIfNotEmpty(_modifiers, FunSpec.Builder::addModifiers)
            .addIfNotNull(_receiverType, TypeNameDeclaration::toTypeName, FunSpec.Builder::receiver)
            .addIfNotNull(_returnType, TypeNameDeclaration::toTypeName, FunSpec.Builder::returns)
            .addFolding(_typeVariables, TypeVariableNameDeclaration::toTypeName, FunSpec.Builder::addTypeVariable)
            .addFolding(_parameters, ParameterDeclaration::toParameterSpec, FunSpec.Builder::addParameter)
            .addFolding(_annotations, AnnotationDeclaration::toAnnotationSpec, FunSpec.Builder::addAnnotation)
            .addIfNotNull(_delegateConstructor) {
                if (_delegateConstructor == "super") callSuperConstructor(_delegateConstructorArguments.map(CodeDeclaration::toCodeBlock))
                else callThisConstructor(_delegateConstructorArguments.map(CodeDeclaration::toCodeBlock))
            }
            .build()
        return _funSpec!!
    }

    override fun refresh() {
        _funSpec = null
        updateChildren()
        members.forEach(Declaration::refresh)
    }

    companion object {
        fun fromFunSpec(functionSpec: FunSpec): FunctionDeclaration = FunctionDeclarationImpl(
            name = functionSpec.name,
            receiverType = functionSpec.receiverType?.toDeclaration(),
            returnType = functionSpec.returnType?.toDeclaration(),
            typeVariables = functionSpec.typeVariables.map(TypeVariableName::toDeclaration),
            parameters = functionSpec.parameters.map(ParameterSpec::toDeclaration),
            delegateConstructor = functionSpec.delegateConstructor,
            delegateConstructorArguments = functionSpec.delegateConstructorArguments.map(CodeBlock::toDeclaration),
            annotations = functionSpec.annotations.map(AnnotationSpec::toDeclaration),
            modifiers = functionSpec.modifiers
        )
    }

}