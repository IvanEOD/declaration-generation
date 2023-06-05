package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.*


/**
 *  Class Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:43 PM EST )
 */
sealed interface ClassDeclaration : DeclarationWithJsName, DeclarationWithModifiers {

    val isCompanion: Boolean
    val isEnum: Boolean

    val kind: TypeSpec.Kind
    val companionObject: ClassDeclaration?
    val superclass: TypeNameDeclaration

    val superinterfaces: Set<SuperInterfaceDeclaration>
    val typeVariables: Set<TypeVariableNameDeclaration>
    val primaryConstructor: FunctionDeclaration?
    val secondaryConstructors: Set<FunctionDeclaration>
    val functions: Set<FunctionDeclaration>
    val properties: Set<PropertyDeclaration>
    val functionsAndProperties: Set<DeclarationWithJsName>
    val classes: Set<ClassDeclaration>

    fun isSameAs(other: ClassDeclaration): Boolean
    fun isSameAs(other: TypeSpec): Boolean

    fun addSuperinterface(superinterface: SuperInterfaceDeclaration)
    fun addSuperinterface(superinterface: Pair<TypeName, CodeBlock?>)
    fun addSuperinterface(typeName: TypeNameDeclaration, delegate: CodeDeclaration? = null)
    fun addSuperinterface(typeName: TypeName, delegate: CodeBlock? = null)

    fun addTypeVariable(typeVariable: TypeVariableNameDeclaration)
    fun addTypeVariable(typeVariable: TypeVariableName)

    fun addFunction(function: FunctionDeclaration)
    fun addFunction(function: FunSpec)

    fun addProperty(property: PropertyDeclaration)
    fun addProperty(property: PropertySpec)

    fun addClass(klass: ClassDeclaration)
    fun addClass(klass: TypeSpec)

    fun addConstructor(constructor: FunctionDeclaration)
    fun addConstructor(constructor: FunSpec)

    fun removeSuperinterface(superinterface: SuperInterfaceDeclaration)
    fun removeSuperinterface(superinterface: Pair<TypeName, CodeBlock?>)
    fun removeSuperinterface(typeName: TypeNameDeclaration, delegate: CodeDeclaration? = null)
    fun removeSuperinterface(typeName: TypeName, delegate: CodeBlock? = null)

    fun removeTypeVariable(typeVariable: TypeVariableNameDeclaration)
    fun removeTypeVariable(typeVariable: TypeVariableName)

    fun removeFunction(function: FunctionDeclaration)
    fun removeFunction(function: FunSpec)

    fun removeProperty(property: PropertyDeclaration)
    fun removeProperty(property: PropertySpec)

    fun removeClass(klass: ClassDeclaration)
    fun removeClass(klass: TypeSpec)

    fun removeConstructor(constructor: FunctionDeclaration)
    fun removeConstructor(constructor: FunSpec)

    fun hasSuperType(vararg names: String): Boolean
    fun hasSuperType(vararg names: TypeNameDeclaration): Boolean

    fun toTypeSpec(): TypeSpec


    companion object {

        fun fromTypeSpec(typeSpec: TypeSpec): ClassDeclaration = ClassDeclarationImpl.fromTypeSpec(typeSpec)

    }
}

