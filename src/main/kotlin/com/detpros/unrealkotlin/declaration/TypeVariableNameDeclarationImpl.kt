package com.detpros.unrealkotlin.declaration

import com.detpros.unrealkotlin.utility.isSameAs
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName


/**
 *  Type Variable Name Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 11:01 AM EST )
 */

internal class TypeVariableNameDeclarationImpl(
    name: String,
    bounds: Set<TypeNameDeclaration>,
    variance: VarianceDeclaration?,
    annotations: Set<AnnotationDeclaration>,
    nullable: Boolean,
    reified: Boolean,
): TypeVariableNameDeclaration {
    private var _name: String = name
    private val _bounds = bounds.toMutableSet()
    private var _variance: VarianceDeclaration? = variance
    private val _annotations = annotations.toMutableSet()
    private var _nullable: Boolean = nullable
    private var _reified: Boolean = reified
    override val name: String get() = _name
    override val bounds: Set<TypeNameDeclaration> get() = _bounds
    override val variance: VarianceDeclaration? get() = _variance
    override val annotations: Set<AnnotationDeclaration> get() = _annotations
    override val nullable: Boolean get() = _nullable
    override val reified: Boolean get() = _reified

    override fun isName(name: String): Boolean = toTypeName().toString() == name

    override fun isSameAs(other: TypeVariableName): Boolean =
        other.name == _name &&
        bounds.isSameAs(other.bounds, TypeNameDeclaration::isSameAs) &&
        other.variance == _variance &&
        other.annotations.size == _annotations.size &&
        other.isNullable == _nullable &&
        other.isReified == _reified

    override fun isSameAs(other: TypeVariableNameDeclaration): Boolean =
        other.name == _name &&
        bounds.isSameAs(other.bounds, TypeNameDeclaration::isSameAs) &&
        other.variance == _variance &&
        other.annotations.size == _annotations.size &&
        other.nullable == _nullable &&
        other.reified == _reified

    override fun isSameAs(typeName: TypeName): Boolean =
        if (typeName !is TypeVariableName) false else isSameAs(typeName)


    override fun isSameAs(other: TypeNameDeclaration): Boolean =
        if (other !is TypeVariableNameDeclaration) false else isSameAs(other)

    private var _typeVariableName: TypeVariableName? = null

    override fun allNames(): List<String> {
        val names = mutableListOf(_name)
        _bounds.forEach { names.addAll(it.allNames()) }
        return names
    }

    override fun toTypeName(): TypeVariableName {
        if (_typeVariableName == null) _typeVariableName = TypeVariableName.invoke(
            name = _name,
            variance = _variance?.toModifier(),
            bounds = _bounds.map(TypeNameDeclaration::toTypeName)
        ).copy(
            nullable = _nullable,
            reified = _reified,
            annotations = _annotations.map(AnnotationDeclaration::toAnnotationSpec),
            tags = emptyMap()
        )
        return _typeVariableName!!
    }

    override fun refresh() {
        _typeVariableName = null
        members.forEach(Declaration::refresh)
    }

    override val members: Set<Declaration> get() = bounds + annotations

    companion object {
        fun fromTypeVariableName(typeVariableName: TypeVariableName): TypeVariableNameDeclaration = TypeVariableNameDeclarationImpl(
            name = typeVariableName.name,
            bounds = typeVariableName.bounds.map(TypeNameDeclaration::fromTypeName).toSet(),
            variance = typeVariableName.variance?.let {
                when (it) {
                    KModifier.IN -> VarianceDeclaration.In
                    KModifier.OUT -> VarianceDeclaration.Out
                    else -> null
                }
            },
            annotations = typeVariableName.annotations.map(AnnotationDeclaration::fromAnnotationSpec).toSet(),
            nullable = typeVariableName.isNullable,
            reified = typeVariableName.isReified
        )
    }

}