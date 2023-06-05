package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.TypeName


/**
 *  Lambda Type Name Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 11:00 AM EST )
 */
internal class LambdaTypeNameDeclarationImpl(
    returnType: TypeNameDeclaration,
    annotations: Set<AnnotationDeclaration> = emptySet(),
    parameters: Set<ParameterDeclaration> = emptySet(),
    receiver: TypeNameDeclaration? = null,
    suspending: Boolean = false,
    nullable: Boolean = false,
): LambdaTypeNameDeclaration {
    private val _annotations = annotations.toMutableSet()
    private val _parameters = parameters.toMutableSet()
    private var _receiver: TypeNameDeclaration? = receiver
    private var _returnType: TypeNameDeclaration = returnType
    private var _suspending: Boolean = suspending
    private var _nullable: Boolean = nullable
    override val annotations: Set<AnnotationDeclaration> get() = _annotations
    override val parameters: Set<ParameterDeclaration> get() = _parameters
    override val receiver: TypeNameDeclaration? get() = _receiver
    override val returnType: TypeNameDeclaration get() = _returnType
    override val suspending: Boolean get() = _suspending
    override val nullable: Boolean get() = _nullable

    override fun isName(name: String): Boolean = toTypeName().toString() == name

    override fun isSameAs(typeName: TypeName): Boolean = if (typeName is LambdaTypeName) isSameAs(typeName) else false
    override fun isSameAs(other: TypeNameDeclaration): Boolean = if (other is LambdaTypeNameDeclaration) isSameAs(other) else false
    override fun isSameAs(other: LambdaTypeNameDeclaration): Boolean = other.toString() == toString()
    override fun isSameAs(other: LambdaTypeName): Boolean = other.toString() == toTypeName().toString()

    override fun toString(): String = toTypeName().toString()

    private var _lambdaTypeName: LambdaTypeName? = null

    override fun allNames(): List<String> {
        val names = mutableListOf<String>()
        _receiver?.let { names.addAll(it.allNames()) }
        _parameters.forEach { parameter -> names.addAll(parameter.typeNames.flatMap { it.allNames() }) }
        if (_returnType != TypeNameDeclaration.Any) names.addAll(_returnType.allNames())
        return names
    }

    private val ParameterDeclaration.typeNames get() = members.filterIsInstance<TypeNameDeclaration>()

    override fun toTypeName(): LambdaTypeName {
        if (_lambdaTypeName == null) _lambdaTypeName = LambdaTypeName.get(
            _receiver?.toTypeName(),
            _parameters.map(ParameterDeclaration::toParameterSpec),
            returnType.toTypeName()
        ).copy(_nullable, _annotations.map(AnnotationDeclaration::toAnnotationSpec), _suspending)
        return _lambdaTypeName!!
    }

    override fun refresh() {
        _lambdaTypeName = null
        members.forEach(Declaration::refresh)
    }

    override val members: Set<Declaration> get() = annotations + parameters + setOfNotNull(receiver, returnType)


    companion object {
        fun fromLambdaTypeName(lambdaTypeName: LambdaTypeName): LambdaTypeNameDeclaration =
            LambdaTypeNameDeclarationImpl(
                returnType = TypeNameDeclaration.fromTypeName(lambdaTypeName.returnType),
                annotations = lambdaTypeName.annotations.map(AnnotationDeclaration::fromAnnotationSpec).toSet(),
                parameters = lambdaTypeName.parameters.map(ParameterDeclaration::fromParameterSpec).toSet(),
                receiver = lambdaTypeName.receiver?.let(TypeNameDeclaration::fromTypeName),
                suspending = lambdaTypeName.isSuspending,
                nullable = lambdaTypeName.isNullable
            )
    }

}