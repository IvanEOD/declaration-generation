package com.detpros.unrealkotlin.declaration

import com.detpros.unrealkotlin.utility.addIfNotEmpty
import com.detpros.unrealkotlin.utility.isSameAs
import com.squareup.kotlinpoet.*


/**
 *  Type Alias Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 11:01 AM EST )
 */
class TypeAliasDeclarationImpl(
    name: String,
    type: TypeNameDeclaration,
    annotations: Collection<AnnotationDeclaration> = emptyList(),
    typeVariables: Collection<TypeVariableNameDeclaration> = emptyList(),
    modifiers: Collection<KModifier> = emptyList(),
): TypeAliasDeclaration {
    private var _lockedRenaming = false
    private var _name = name
    private var _type = type
    private val _annotations = annotations.toMutableSet()
    private val _typeVariables = typeVariables.toMutableSet()
    private val _modifiers = modifiers.toMutableSet()

    override val originalName: String = name
    override val name: String get() = _name
    override val members: Set<Declaration> get() = setOf(_type) + annotations
    override val annotations: Set<AnnotationDeclaration> get() = _annotations
    override val modifiers: Set<KModifier> get() = _modifiers
    override val typeVariables: Set<TypeVariableNameDeclaration> get() = _typeVariables
    override val type: TypeNameDeclaration get() = _type
    override val isRenamingLocked: Boolean get() = _lockedRenaming

    override val isTopLevel: Boolean = true

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

    override fun rename(name: String) {
        if (_lockedRenaming) return
        val oldName = _name
        _name = name
        ClassNameDeclaration.renameClass(oldName, name)
    }

    override fun lockRenaming() {
        _lockedRenaming = true
    }

    override fun unlockRenaming() {
        _lockedRenaming = false
    }

    override fun isSameAs(other: TypeAliasDeclaration): Boolean {
        other as TypeAliasDeclarationImpl
        return _name == other._name &&
            _type.isSameAs(other._type) &&
            _annotations.isSameAs(other._annotations, AnnotationDeclaration::isSameAs) &&
            _typeVariables.isSameAs(other._typeVariables, TypeVariableNameDeclaration::isSameAs) &&
            _modifiers == other._modifiers
    }

    override fun isSameAs(other: TypeAliasSpec): Boolean =
        _name == other.name &&
        _type.isSameAs(other.type) &&
        _annotations.isSameAs(other.annotations, AnnotationDeclaration::isSameAs) &&
        _typeVariables.isSameAs(other.typeVariables, TypeVariableNameDeclaration::isSameAs) &&
        _modifiers == other.modifiers

    private var _typeAlias: TypeAliasSpec? = null

    override fun toTypeAliasSpec(): TypeAliasSpec {
        if (_typeAlias == null) _typeAlias = TypeAliasSpec.builder(_name, _type.toTypeName())
            .addIfNotEmpty(_annotations, AnnotationDeclaration::toAnnotationSpec, TypeAliasSpec.Builder::addAnnotations)
            .addIfNotEmpty(_typeVariables, TypeVariableNameDeclaration::toTypeName, TypeAliasSpec.Builder::addTypeVariables)
            .addIfNotEmpty(_modifiers, TypeAliasSpec.Builder::addModifiers)
            .build()
        return _typeAlias!!
    }

    override fun refresh() {
        _typeAlias = null
        members.forEach(Declaration::refresh)
    }

    companion object {
        fun fromTypeAliasSpec(typeAliasSpec: TypeAliasSpec): TypeAliasDeclaration = TypeAliasDeclarationImpl(
            typeAliasSpec.name,
            typeAliasSpec.type.toDeclaration(),
            typeAliasSpec.annotations.map(AnnotationSpec::toDeclaration),
            typeAliasSpec.typeVariables.map(TypeVariableName::toDeclaration),
            typeAliasSpec.modifiers
        )
    }

}