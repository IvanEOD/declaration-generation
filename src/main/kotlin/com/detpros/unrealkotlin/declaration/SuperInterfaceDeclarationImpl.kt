package com.detpros.unrealkotlin.declaration

import com.detpros.unrealkotlin.utility.isSameAs
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName


/**
 *  Super Interface Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 11:00 AM EST )
 */
class SuperInterfaceDeclarationImpl(
    type: TypeNameDeclaration,
    delegate: CodeDeclaration?
): SuperInterfaceDeclaration {
    private var _type = type
    private var _delegate = delegate

    override val type: TypeNameDeclaration get() = _type
    override val delegate: CodeDeclaration? get() = _delegate

    private var _superinterface: Pair<TypeName, CodeBlock?>? = null

    override val members: Set<Declaration> = setOfNotNull(_type, _delegate)

    override fun toSuperInterface(): Pair<TypeName, CodeBlock?> {
        if (_superinterface == null) _superinterface = _type.toTypeName() to _delegate?.toCodeBlock()
        return _superinterface!!
    }

    override fun refresh() {
        _superinterface = null
        members.forEach(Declaration::refresh)
    }



    override fun isSameAs(other: SuperInterfaceDeclaration): Boolean =
        _type.isSameAs(other.type) && _delegate.isSameAs(other.delegate, CodeDeclaration::isSameAs)

    override fun isSameAs(other: Pair<TypeName, CodeBlock?>): Boolean {
        val (otherType, otherDelegate) = other
        return _type.isSameAs(otherType) && _delegate.isSameAs(otherDelegate, CodeDeclaration::isSameAs)
    }

    override fun isSameAs(other: Map.Entry<TypeName, CodeBlock?>): Boolean {
        val (otherType, otherDelegate) = other
        return _type.isSameAs(otherType) && _delegate.isSameAs(otherDelegate, CodeDeclaration::isSameAs)
    }

    companion object {
        fun fromSuperInterface(typeName: TypeNameDeclaration, codeBlock: CodeDeclaration?): SuperInterfaceDeclaration =
            SuperInterfaceDeclarationImpl(typeName, codeBlock)
        fun fromSuperInterface(typeName: TypeName, codeBlock: CodeBlock?): SuperInterfaceDeclaration =
            SuperInterfaceDeclarationImpl(typeName.toDeclaration(), codeBlock?.toDeclaration())
        fun fromSuperInterface(entry: Map.Entry<TypeName, CodeBlock?>): SuperInterfaceDeclaration =
            fromSuperInterface(entry.key, entry.value)
    }

}