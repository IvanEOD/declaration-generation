package com.detpros.unrealkotlin.declaration

import com.detpros.unrealkotlin.utility.addIfNotEmpty
import com.detpros.unrealkotlin.utility.addIfNotNull
import com.detpros.unrealkotlin.utility.isSameAs
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeVariableName
import org.jetbrains.kotlin.utils.addToStdlib.applyIf


/**
 *  Property Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 11:00 AM EST )
 */
internal class PropertyDeclarationImpl(
    name: String,
    type: TypeNameDeclaration,
    receiverType: TypeNameDeclaration?,
    typeVariables: Collection<TypeVariableNameDeclaration>,
    initializer: CodeDeclaration?,
    getter: FunctionDeclaration?,
    setter: FunctionDeclaration?,
    annotations: Collection<AnnotationDeclaration>,
    modifiers: Collection<KModifier>,
    mutable: Boolean,
    delegated: Boolean,
) : BaseJsNameDeclaration(), PropertyDeclaration, ChildDeclaration {
    private var _type = type
    private var _receiverType = receiverType
    private val _typeVariables = typeVariables.toMutableSet()
    private var _initializer = initializer
    private var _getter = getter
    private var _setter = setter
    private val _modifiers = modifiers.toMutableSet()

    private var _isMutable = mutable
    private var _isDelegated = delegated


    init {
        _name = name
        _annotations.addAll(annotations)
    }

    override var _parent: ParentDeclaration? = null
    override val members: Set<Declaration>
        get() = setOfNotNull(
            _receiverType,
            _type,
            _initializer,
            _getter,
            _setter
        ) + _typeVariables

    override val annotations: Set<AnnotationDeclaration> get() = _annotations
    override val modifiers: Set<KModifier> get() = _modifiers
    override val originalName: String = name
    override val name: String get() = _name
    override val type: TypeNameDeclaration get() = _type
    override val receiverType: TypeNameDeclaration? get() = _receiverType
    override val typeVariables: Set<TypeVariableNameDeclaration> get() = _typeVariables
    override val initializer: CodeDeclaration? get() = _initializer
    override val getter: FunctionDeclaration? get() = _getter
    override val setter: FunctionDeclaration? get() = _setter
    override val isMutable: Boolean get() = _isMutable
    override val isDelegated: Boolean get() = _isDelegated

    private var _propertySpec: PropertySpec? = null

    override val isJsNameAllowed: Boolean
        get() = !isOverride

    override fun refresh() {
        _propertySpec = null
        updateChildren()
        members.forEach(Declaration::refresh)
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

    override fun isType(type: String): Boolean = _type.isName(type)
    override fun isType(type: TypeNameDeclaration): Boolean = _type.isSameAs(type)

    override fun changeType(type: String) = changeType(ClassNameDeclaration.getClassName(type))
    override fun changeType(type: TypeNameDeclaration) {
        _type = type
    }

    override fun isSameAs(other: PropertyDeclaration): Boolean {
        other as PropertyDeclarationImpl
        return _name == other.name &&
            _type.isSameAs(other.type) &&
            _receiverType.isSameAs(other._receiverType, TypeNameDeclaration::isSameAs) &&
            _typeVariables.isSameAs(other._typeVariables, TypeVariableNameDeclaration::isSameAs) &&
            _initializer.isSameAs(other._initializer, CodeDeclaration::isSameAs) &&
            _getter.isSameAs(other._getter, FunctionDeclaration::isSameAs) &&
            _setter.isSameAs(other._setter, FunctionDeclaration::isSameAs) &&
            _annotations.isSameAs(other._annotations, AnnotationDeclaration::isSameAs) &&
            _modifiers.isSameAs(other._modifiers, KModifier::equals) &&
            _isMutable == other.isMutable &&
            _isDelegated == other.isDelegated
    }

    override fun isSameAs(other: PropertySpec): Boolean =
        _name == other.name &&
        _type.isSameAs(other.type) &&
        _receiverType.isSameAs(other.receiverType, TypeNameDeclaration::isSameAs) &&
        _typeVariables.isSameAs(other.typeVariables, TypeVariableNameDeclaration::isSameAs) &&
        _initializer.isSameAs(other.initializer, CodeDeclaration::isSameAs) &&
        _getter.isSameAs(other.getter, FunctionDeclaration::isSameAs) &&
        _setter.isSameAs(other.setter, FunctionDeclaration::isSameAs) &&
        _annotations.isSameAs(other.annotations, AnnotationDeclaration::isSameAs) &&
        _modifiers.isSameAs(other.modifiers, KModifier::equals) &&
        _isMutable == other.mutable &&
        _isDelegated == other.delegated

    override fun toPropertySpec(): PropertySpec {
        if (_propertySpec == null) _propertySpec = PropertySpec.builder(_name, _type.toTypeName(), *_modifiers.toTypedArray())
            .addIfNotEmpty(_annotations, AnnotationDeclaration::toAnnotationSpec, PropertySpec.Builder::addAnnotations)
            .addIfNotEmpty(_typeVariables, TypeVariableNameDeclaration::toTypeName, PropertySpec.Builder::addTypeVariables)
            .addIfNotNull(_initializer, CodeDeclaration::toCodeBlock) {
                if (isDelegated) delegate(it) else initializer(it)
            }
            .addIfNotNull(_getter, FunctionDeclaration::toFunSpec, PropertySpec.Builder::getter)
            .addIfNotNull(_setter, FunctionDeclaration::toFunSpec, PropertySpec.Builder::setter)
            .addIfNotNull(_receiverType, TypeNameDeclaration::toTypeName, PropertySpec.Builder::receiver)
            .applyIf(_isMutable) { mutable(true) }
            .build()
        return _propertySpec!!
    }




    companion object {
        fun fromPropertySpec(propertySpec: PropertySpec): PropertyDeclaration = PropertyDeclarationImpl(
            propertySpec.name,
            propertySpec.type.toDeclaration(),
            propertySpec.receiverType?.toDeclaration(),
            propertySpec.typeVariables.map(TypeVariableName::toDeclaration),
            propertySpec.initializer?.toDeclaration(),
            propertySpec.getter?.toDeclaration(),
            propertySpec.setter?.toDeclaration(),
            propertySpec.annotations.map(AnnotationSpec::toDeclaration),
            propertySpec.modifiers,
            propertySpec.mutable,
            propertySpec.delegated
        )
    }

}