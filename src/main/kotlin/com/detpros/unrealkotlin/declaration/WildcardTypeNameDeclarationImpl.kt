package com.detpros.unrealkotlin.declaration

import com.detpros.unrealkotlin.utility.isSameAs
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName


/**
 *  Wildcard Type Name Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 11:01 AM EST )
 */

internal class WildcardTypeNameDeclarationImpl(
    inTypes: Set<TypeNameDeclaration> = emptySet(),
    outTypes: Set<TypeNameDeclaration> = emptySet(),
    annotations: Set<AnnotationDeclaration> = emptySet(),
    nullable: Boolean = false,
): WildcardTypeNameDeclaration {
    private val _inTypes = inTypes.toMutableSet()
    private val _outTypes = outTypes.toMutableSet()
    private val _annotations = annotations.toMutableSet()
    private var _nullable: Boolean = nullable
    override val inTypes: Set<TypeNameDeclaration> get() = _inTypes
    override val outTypes: Set<TypeNameDeclaration> get() = _outTypes
    override val annotations: Set<AnnotationDeclaration> get() = _annotations
    override val nullable: Boolean get() = _nullable

    override fun isName(name: String): Boolean = toTypeName().toString() == name

    override fun isSameAs(typeName: TypeName): Boolean = typeName is WildcardTypeName && isSameAs(typeName)
    override fun isSameAs(other: TypeNameDeclaration): Boolean = other is WildcardTypeNameDeclaration && isSameAs(other)

    override fun isSameAs(other: WildcardTypeNameDeclaration): Boolean =
        inTypes.isSameAs(other.inTypes, TypeNameDeclaration::isSameAs) &&
        outTypes.isSameAs(other.outTypes, TypeNameDeclaration::isSameAs) &&
        annotations.isSameAs(other.annotations, AnnotationDeclaration::isSameAs) &&
        nullable == other.nullable

    override fun isSameAs(other: WildcardTypeName): Boolean =
        inTypes.isSameAs(other.inTypes, TypeNameDeclaration::isSameAs) &&
        outTypes.isSameAs(other.outTypes, TypeNameDeclaration::isSameAs) &&
        annotations.isSameAs(other.annotations, AnnotationDeclaration::isSameAs) &&
        nullable == other.isNullable

    private var _wildcardTypeName: WildcardTypeName? = null

    override fun toTypeName(): WildcardTypeName {
        if (_wildcardTypeName == null) _wildcardTypeName = if (_outTypes.isNotEmpty()) WildcardTypeName.producerOf(_outTypes.map(TypeNameDeclaration::toTypeName).first())
        else WildcardTypeName.consumerOf(_inTypes.map(TypeNameDeclaration::toTypeName).first())
            .copy(
                nullable = _nullable,
                annotations = _annotations.map(AnnotationDeclaration::toAnnotationSpec),
                tags = emptyMap()
            )
        return _wildcardTypeName!!
    }

    override fun refresh() {
        _wildcardTypeName = null
        members.forEach(Declaration::refresh)
    }

    override fun allNames(): List<String> {
        val names = mutableListOf("Wildcard")
        _inTypes.forEach { names.addAll(it.allNames().map { name -> "In$name" }) }
        _outTypes.forEach { names.addAll(it.allNames().map { name -> "Out$name" }) }
        return names
    }

    override val members: Set<Declaration> get() = annotations + inTypes + outTypes

    companion object {
        fun fromWildcardTypeName(wildcardTypeName: WildcardTypeName): WildcardTypeNameDeclaration = WildcardTypeNameDeclarationImpl(
            inTypes = if (wildcardTypeName.inTypes.isNotEmpty()) setOf(TypeNameDeclaration.fromTypeName(wildcardTypeName.inTypes.first())) else emptySet(),
            outTypes = if (wildcardTypeName.outTypes.isNotEmpty()) setOf(TypeNameDeclaration.fromTypeName(wildcardTypeName.outTypes.first())) else emptySet(),
            annotations = wildcardTypeName.annotations.map(AnnotationDeclaration::fromAnnotationSpec).toSet(),
            nullable = wildcardTypeName.isNullable
        )
    }

}