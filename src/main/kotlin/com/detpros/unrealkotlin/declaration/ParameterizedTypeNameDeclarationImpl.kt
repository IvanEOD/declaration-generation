package com.detpros.unrealkotlin.declaration

import com.detpros.unrealkotlin.utility.isSameAs
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName


/**
 *  Parameterized Type Name Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 11:00 AM EST )
 */
internal class ParameterizedTypeNameDeclarationImpl(
    rawType: ClassNameDeclaration,
    typeArguments: Set<TypeNameDeclaration> = emptySet(),
    nullable: Boolean = false,
    annotations: Set<AnnotationDeclaration> = emptySet(),
): ParameterizedTypeNameDeclaration {
    private val _rawType: ClassNameDeclaration = rawType
    private val _typeArguments: Set<TypeNameDeclaration> = typeArguments
    private var _nullable: Boolean = nullable
    private val _annotations: Set<AnnotationDeclaration> = annotations
    override val rawType: ClassNameDeclaration get() = _rawType
    override val typeArguments: Set<TypeNameDeclaration> get() = _typeArguments
    override val nullable: Boolean get() = _nullable
    override val annotations: Set<AnnotationDeclaration> get() = _annotations

    override fun isName(name: String): Boolean = toTypeName().toString() == name
    override fun toString(): String = toTypeName().toString()

    private var _typeName: ParameterizedTypeName? = null
    override fun toTypeName(): ParameterizedTypeName {
        if (_typeName == null) _typeName = _rawType.toTypeName().parameterizedBy(_typeArguments.map(TypeNameDeclaration::toTypeName))
            .copy(nullable = _nullable, annotations = _annotations.map(AnnotationDeclaration::toAnnotationSpec), tags = emptyMap())
        return _typeName!!
    }

    override fun refresh() {
        _typeName = null
        members.forEach(Declaration::refresh)
    }

    override fun allNames(): List<String> {
        val names = mutableListOf<String>()
        names.addAll(_rawType.allNames())
        _typeArguments.forEach { names.addAll(it.allNames()) }
        return names
    }

    override fun isSameAs(other: ParameterizedTypeNameDeclaration): Boolean {
        other as ParameterizedTypeNameDeclarationImpl
        return _rawType.isSameAs(other._rawType) &&
                _typeArguments.isSameAs(other._typeArguments, TypeNameDeclaration::isSameAs) &&
                _nullable == other._nullable &&
                _annotations.isSameAs(other._annotations, AnnotationDeclaration::isSameAs)
    }
    override fun isSameAs(other: ParameterizedTypeName): Boolean =
        _rawType.isSameAs(other.rawType) &&
        _typeArguments.isSameAs(other.typeArguments, TypeNameDeclaration::isSameAs) &&
        _nullable == other.isNullable &&
        _annotations.isSameAs(other.annotations, AnnotationDeclaration::isSameAs)


    override fun isSameAs(typeName: TypeName): Boolean = if (typeName is ParameterizedTypeName) isSameAs(typeName) else false
    override fun isSameAs(other: TypeNameDeclaration): Boolean = if (other is ParameterizedTypeNameDeclaration) isSameAs(other) else false

    override val members: Set<Declaration> get() = setOf(_rawType) + _typeArguments + _annotations

    companion object {
        fun fromParameterizedTypeName(parameterizedTypeName: ParameterizedTypeName): ParameterizedTypeNameDeclaration =
            ParameterizedTypeNameDeclarationImpl(
                parameterizedTypeName.rawType.toDeclaration(),
                parameterizedTypeName.typeArguments.map(TypeNameDeclaration::fromTypeName).toSet(),
                parameterizedTypeName.isNullable,
                parameterizedTypeName.annotations.map(AnnotationDeclaration::fromAnnotationSpec).toSet()
            )
    }

}