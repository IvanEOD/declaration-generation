package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.*
import java.io.File


/**
 *  File Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:44 PM EST )
 */
sealed interface FileDeclaration : DeclarationWithAnnotations, DeclarationWithName {

    override val name: String
    val packageName: String
    val defaultImports: Set<String>
    val classes: Set<ClassDeclaration>
    val typeAliases: Set<TypeAliasDeclaration>
    val functions: Set<FunctionDeclaration>
    val properties: Set<PropertyDeclaration>

    fun isSameAs(other: FileDeclaration): Boolean
    fun isSameAs(other: FileSpec): Boolean

    override fun rename(name: String)
    override fun lockRenaming()
    fun addDeclaration(declaration: Declaration)
    fun addClass(klass: ClassDeclaration)
    fun addClass(klass: TypeSpec)
    fun addFunction(function: FunctionDeclaration)
    fun addFunction(function: FunSpec)
    fun addProperty(property: PropertyDeclaration)
    fun addProperty(property: PropertySpec)
    fun addTypeAlias(typeAlias: TypeAliasDeclaration)
    fun addTypeAlias(typeAlias: TypeAliasSpec)

    fun removeDeclaration(declaration: Declaration)
    fun removeDeclarations(vararg declaration: Declaration)
    fun removeClass(klass: ClassDeclaration)
    fun removeClass(klass: TypeSpec)
    fun removeFunction(function: FunctionDeclaration)
    fun removeFunction(function: FunSpec)
    fun removeProperty(property: PropertyDeclaration)
    fun removeProperty(property: PropertySpec)
    fun removeTypeAlias(typeAlias: TypeAliasDeclaration)
    fun removeTypeAlias(typeAlias: TypeAliasSpec)

    fun write(destination: File)

    fun toFileSpec(): FileSpec

    companion object {

        fun fromFileSpec(fileSpec: FileSpec): FileDeclaration = FileDeclarationImpl.fromFileSpec(fileSpec)
    }
}

