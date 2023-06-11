package com.detpros.unrealkotlin.declaration

import com.detpros.unrealkotlin.utility.addIfNotEmpty
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec


/**
 *  Parameter Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 11:00 AM EST )
 */

internal class ParameterDeclarationImpl(
    name: String,
    type: TypeNameDeclaration,
    modifiers: Collection<KModifier> = emptySet(),
    annotations: Collection<AnnotationDeclaration> = emptySet(),
): ParameterDeclaration, ChildDeclaration {
    private var _lockedRenaming = false
    private var _name = name
    private var _type: TypeNameDeclaration = type
    private val _modifiers: MutableSet<KModifier> = modifiers.toMutableSet()
    private val _annotations: MutableSet<AnnotationDeclaration> = annotations.toMutableSet()
    override var _parent: ParentDeclaration? = null
    override val originalName: String = name
    override val name: String get() = _name
    override val type: TypeNameDeclaration get() = _type
    override val modifiers: Set<KModifier> get() = _modifiers
    override val annotations: Set<AnnotationDeclaration> get() = _annotations
    override val members: Set<Declaration> get() = annotations + type

    override fun isVariableType(): Boolean = type is TypeVariableNameDeclaration
    override fun rename(caller: String, name: String) {
        _name = name
    }

    override fun addAnnotation(annotation: AnnotationDeclaration) {
        _annotations.add(annotation)
    }

    override fun removeAnnotation(annotation: AnnotationDeclaration) {
        _annotations.remove(annotation)
    }

    override fun clearAnnotations() {
        _annotations.clear()
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

    override fun setTypeVariableToBound() {
        if (_type is TypeVariableNameDeclaration) _type = (_type as TypeVariableNameDeclaration).bounds.first()
    }

    override fun changeType(typeName: String) {
        _type = ClassNameDeclaration.getClassName(typeName)
    }

    override fun changeType(typeName: TypeNameDeclaration) {
        _type = typeName
    }

    override fun isSameAs(declaration: ParameterDeclaration): Boolean =
        _name == declaration.name && _type.isSameAs(declaration.type)

    override fun isSameAs(spec: ParameterSpec): Boolean =
        _name == spec.name && _type.isSameAs(spec.type)

    override fun removeTypeVariable(typeVariable: TypeVariableNameDeclaration) {
        if (_type is TypeVariableNameDeclaration) {
            val typeVariableNameDeclaration = _type as TypeVariableNameDeclaration
            if (typeVariableNameDeclaration.name == typeVariable.name) {
                _type = typeVariableNameDeclaration.bounds.first()
            }
        }
    }

    private var _parameterSpec: ParameterSpec? = null

    override fun toParameterSpec(): ParameterSpec {
        if (_parameterSpec == null) _parameterSpec = ParameterSpec.builder(name, type.toTypeName())
            .addIfNotEmpty(_modifiers, ParameterSpec.Builder::addModifiers)
            .addIfNotEmpty(_annotations, AnnotationDeclaration::toAnnotationSpec, ParameterSpec.Builder::addAnnotations)
            .build()
        return _parameterSpec!!
    }

    override fun refresh() {
        _parameterSpec = null
        members.forEach(Declaration::refresh)
    }

    override fun lockRenaming() {
        _lockedRenaming = true
    }

    override fun unlockRenaming() {
        _lockedRenaming = false
    }

    override val isRenamingLocked: Boolean get() = _lockedRenaming

    companion object {
        fun fromParameterSpec(parameterSpec: ParameterSpec): ParameterDeclaration = ParameterDeclarationImpl(
            parameterSpec.name,
            TypeNameDeclaration.fromTypeName(parameterSpec.type),
            parameterSpec.modifiers,
            parameterSpec.annotations.map(AnnotationDeclaration::fromAnnotationSpec)
        )
    }

}