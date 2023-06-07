package com.detpros.unrealkotlin.declaration

import com.detpros.unrealkotlin.utility.addFolding
import com.detpros.unrealkotlin.utility.isSameAs
import com.detpros.unrealkotlin.utility.removeFirst
import com.squareup.kotlinpoet.*
import java.io.File
import java.util.concurrent.atomic.AtomicInteger


/**
 *  File Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 11:00 AM EST )
 */
internal class FileDeclarationImpl(
    packageName: String,
    name: String,
    defaultImports: Collection<String> = emptySet(),
    classes: Collection<ClassDeclaration> = emptySet(),
    typeAliases: Collection<TypeAliasDeclaration> = emptySet(),
    functions: Collection<FunctionDeclaration> = emptySet(),
    properties: Collection<PropertyDeclaration> = emptySet(),
    annotations: Collection<AnnotationDeclaration> = emptySet()
) : ParentDeclaration(), FileDeclaration, DeclarationWithName {

    internal var _packageName = packageName
    private var _name = name
    private var _renamingLocked = false
    private val _defaultImports = defaultImports.toMutableSet()
    private val _classes = classes.toMutableSet()
    private val _typeAliases = typeAliases.toMutableSet()
    private val _functions = functions.toMutableSet()
    private val _properties = properties.toMutableSet()
    private val _annotations = annotations.toMutableSet()

    override val originalName: String = name
    override val packageName: String get() = _packageName
    override val name: String get() = _name
    override val defaultImports: Set<String> get() = _defaultImports
    override val classes: Set<ClassDeclaration> get() = _classes
    override val typeAliases: Set<TypeAliasDeclaration> get() = _typeAliases
    override val functions: Set<FunctionDeclaration> get() = _functions
    override val properties: Set<PropertyDeclaration> get() = _properties
    override val annotations: Set<AnnotationDeclaration> get() = _annotations

    override val members: Set<Declaration> get() = _classes + _typeAliases + _functions + _properties + _annotations


    override fun onChildClaimed(declaration: ChildDeclaration) {

    }

    override fun onChildReleased(declaration: ChildDeclaration) {

    }

    override fun lockRenaming() {
        _renamingLocked = true
    }
    override fun unlockRenaming() {
        _renamingLocked = false
    }

    override val isRenamingLocked: Boolean get() = _renamingLocked

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

    override fun addClass(klass: ClassDeclaration) {
        _classes.add(klass)
    }

    override fun addClass(klass: TypeSpec) = addClass(klass.toDeclaration())
    override fun addFunction(function: FunctionDeclaration) {
        _functions.add(function)
    }

    override fun addFunction(function: FunSpec) = addFunction(function.toDeclaration())

    override fun addProperty(property: PropertyDeclaration) {
        _properties.add(property)
    }

    override fun addProperty(property: PropertySpec) = addProperty(property.toDeclaration())

    override fun addTypeAlias(typeAlias: TypeAliasDeclaration) {
        _typeAliases.add(typeAlias)
    }

    override fun addTypeAlias(typeAlias: TypeAliasSpec) = addTypeAlias(typeAlias.toDeclaration())
    override fun removeClass(klass: ClassDeclaration) {
        _classes.removeFirst { it == klass || it.isSameAs(klass) }
    }

    override fun removeClass(klass: TypeSpec) {
        _classes.removeFirst { it.isSameAs(klass) }
    }

    override fun removeFunction(function: FunctionDeclaration) {
        _functions.removeFirst { it.isSameAs(function) }
    }

    override fun removeFunction(function: FunSpec) {
        _functions.removeFirst { it.isSameAs(function) }
    }

    override fun removeProperty(property: PropertyDeclaration) {
        _properties.removeFirst { it.isSameAs(property) }
    }

    override fun removeProperty(property: PropertySpec) {
        _properties.removeFirst { it.isSameAs(property) }
    }

    override fun removeTypeAlias(typeAlias: TypeAliasDeclaration) {
        _typeAliases.removeFirst { it.isSameAs(typeAlias) }
    }

    override fun removeTypeAlias(typeAlias: TypeAliasSpec) {
        _typeAliases.removeFirst { it.isSameAs(typeAlias) }
    }

    override fun addDeclaration(declaration: Declaration) {
        when (declaration) {
            is ClassDeclaration -> addClass(declaration)
            is FunctionDeclaration -> addFunction(declaration)
            is PropertyDeclaration -> addProperty(declaration)
            is TypeAliasDeclaration -> addTypeAlias(declaration)
            is AnnotationDeclaration -> addAnnotation(declaration)
            else -> {}
        }
    }

    override fun removeDeclaration(declaration: Declaration) {
        when (declaration) {
            is ClassDeclaration -> removeClass(declaration)
            is FunctionDeclaration -> removeFunction(declaration)
            is PropertyDeclaration -> removeProperty(declaration)
            is TypeAliasDeclaration -> removeTypeAlias(declaration)
            is AnnotationDeclaration -> removeAnnotation(declaration)
            else -> {}
        }
    }

    override fun removeDeclarations(vararg declaration: Declaration) {
        declaration.forEach { removeDeclaration(it) }
    }

    override fun isSameAs(other: FileDeclaration): Boolean {
        other as FileDeclarationImpl
        return _packageName == other.packageName &&
            _name == other.name &&
            _defaultImports.isSameAs(other._defaultImports, String::equals) &&
            _classes.isSameAs(other._classes, ClassDeclaration::isSameAs) &&
            _typeAliases.isSameAs(other._typeAliases, TypeAliasDeclaration::isSameAs) &&
            _functions.isSameAs(other._functions, FunctionDeclaration::isSameAs) &&
            _properties.isSameAs(other._properties, PropertyDeclaration::isSameAs) &&
            _annotations.isSameAs(other._annotations, AnnotationDeclaration::isSameAs)
    }

    override fun isSameAs(other: FileSpec): Boolean =
        _packageName == other.packageName &&
        _name == other.name &&
        _defaultImports.isSameAs(other.defaultImports, String::equals) &&
        _classes.isSameAs(other.classes, ClassDeclaration::isSameAs) &&
        _typeAliases.isSameAs(other.typeAliases, TypeAliasDeclaration::isSameAs) &&
        _functions.isSameAs(other.functions, FunctionDeclaration::isSameAs) &&
        _properties.isSameAs(other.properties, PropertyDeclaration::isSameAs) &&
        _annotations.isSameAs(other.annotations, AnnotationDeclaration::isSameAs)

    private var _fileSpec: FileSpec? = null

    override fun toFileSpec(): FileSpec {
        if (_fileSpec == null) _fileSpec = FileSpec.builder(_packageName, _name)
            .addFileComment("%L", "Generated by Detonate Productions Declaration Generation, do not edit manually!")
            .addDefaultAnnotations()
            .addFolding(_defaultImports, FileSpec.Builder::addDefaultPackageImport)
            .addFolding(_classes, ClassDeclaration::toTypeSpec, FileSpec.Builder::addType)
            .addFolding(_typeAliases, TypeAliasDeclaration::toTypeAliasSpec, FileSpec.Builder::addTypeAlias)
            .addFolding(_functions, FunctionDeclaration::toFunSpec, FileSpec.Builder::addFunction)
            .addFolding(_properties, PropertyDeclaration::toPropertySpec, FileSpec.Builder::addProperty)
            .build()
        return _fileSpec!!
    }

    override fun refresh() {
        _fileSpec = null
        updateChildren()
        members.forEach(Declaration::refresh)
    }

    override fun write(destination: File) {
        refresh()
        toFileSpec().writeTo(destination)
//        val fileSpecs = mutableSetOf<FileSpec>()
//        if (members.size > 200) {
//            members.chunked(200).forEachIndexed { index, chunk ->
//                val file = FileDeclarationImpl(
//                    _packageName,
//                    "${_name}_$index",
//                    _defaultImports,
//                    chunk.filterIsInstance<ClassDeclaration>(),
//                    chunk.filterIsInstance<TypeAliasDeclaration>(),
//                    chunk.filterIsInstance<FunctionDeclaration>(),
//                    chunk.filterIsInstance<PropertyDeclaration>(),
//                    _annotations
//                )
//                fileSpecs.add(file.toFileSpec())
//            }
//        } else fileSpecs.add(toFileSpec())

//        fileSpecs.forEach { it.writeTo(destination) }
    }

    companion object {

        fun fromFileSpec(fileSpec: FileSpec): FileDeclaration = FileDeclarationImpl(
            packageName = fileSpec.packageName,
            name = fileSpec.name,
            defaultImports = fileSpec.defaultImports,
            classes = fileSpec.classes.map(TypeSpec::toDeclaration),
            typeAliases = fileSpec.typeAliases.map(TypeAliasSpec::toDeclaration),
            functions = fileSpec.functions.map(FunSpec::toDeclaration),
            properties = fileSpec.properties.map(PropertySpec::toDeclaration),
            annotations = fileSpec.annotations.map(AnnotationSpec::toDeclaration)
        )
    }

}