package com.detpros.unrealkotlin.declaration

import com.detpros.unrealkotlin.*
import com.detpros.unrealkotlin.corrections.UnrealEnumCorrections
import com.detpros.unrealkotlin.utility.*
import com.squareup.kotlinpoet.*
import org.jetbrains.kotlin.utils.addToStdlib.applyIf


/**
 *  Class Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 10:59 AM EST )
 */

internal class ClassDeclarationImpl(
    name: String,
    kind: TypeSpec.Kind,
    superclass: TypeNameDeclaration = TypeNameDeclaration.Any,
    superinterfaces: Collection<SuperInterfaceDeclaration> = emptyList(),
    typeVariables: Collection<TypeVariableNameDeclaration> = emptyList(),
    constructor: FunctionDeclaration? = null,
    secondaryConstructors: Collection<FunctionDeclaration> = emptyList(),
    functions: Collection<FunctionDeclaration> = emptyList(),
    properties: Collection<PropertyDeclaration> = emptyList(),
    classes: Collection<ClassDeclaration> = emptyList(),
    annotations: Collection<AnnotationDeclaration> = emptyList(),
    modifiers: Collection<KModifier> = emptyList(),
    companionObject: ClassDeclaration? = null
): BaseJsNameDeclaration(), ClassDeclaration, ChildDeclaration  {
    override var _parent: ParentDeclaration? = null

    private val _thisClassName = ClassNameDeclaration.getClassName("ue.$name")

    override fun onChildClaimed(declaration: ChildDeclaration) {
    }

    override fun onChildReleased(declaration: ChildDeclaration) {
    }

    override val dependencyNames: Set<String>
        get() = allMembers.filterIsInstance<TypeNameDeclaration>()
                .map { it.allNames().last() }
                .toSet()

    override val isCompanion: Boolean by lazy { hasModifier(KModifier.COMPANION) || originalName == "Companion" }
    override val isEnum: Boolean by lazy { originalName in unrealEnumClassNames }

    private var _kind: TypeSpec.Kind = kind
    private var _companionObject = companionObject
    internal var _superclass = superclass
    private val _superinterfaces = superinterfaces.toMutableSet()
    private val _typeVariables = typeVariables.toMutableSet()
    private var _primaryConstructor = constructor
    private val _secondaryConstructors = secondaryConstructors.toMutableSet()
    private val _functions = functions.toMutableSet()
    private val _properties = properties.toMutableSet()
    private val _classes = classes.toMutableSet()
    private val _modifiers = modifiers.toMutableSet()
    override val functionsAndProperties: Set<DeclarationWithJsName> get() = _functions + _properties

    init {
        _name = name
        _annotations.addAll(annotations)
    }

    override val originalName: String = name
    override val name: String get() = _name
    override val kind: TypeSpec.Kind get() = _kind
    override val companionObject: ClassDeclaration? get() = _companionObject
    override val superclass: TypeNameDeclaration get() = _superclass
    override val superinterfaces: Set<SuperInterfaceDeclaration> get() = _superinterfaces
    override val typeVariables: Set<TypeVariableNameDeclaration> get() = _typeVariables
    override val primaryConstructor: FunctionDeclaration? get() = _primaryConstructor
    override val secondaryConstructors: Set<FunctionDeclaration> get() = _secondaryConstructors
    override val functions: Set<FunctionDeclaration> get() = _functions
    override val properties: Set<PropertyDeclaration> get() = _properties
    override val classes: Set<ClassDeclaration> get() = _classes
    override val annotations: Set<AnnotationDeclaration> get() = _annotations
    override val modifiers: Set<KModifier> get() = _modifiers

    override val members: Set<Declaration> get() = setOfNotNull(_primaryConstructor, _superclass, _companionObject) +
            _annotations + _secondaryConstructors + _functions + _properties + _classes + _annotations +
            _typeVariables + _superinterfaces

    override fun hasSuperType(vararg names: String): Boolean =
        names.any { _superclass.isName(name) || _superinterfaces.any { it.type.isName(name)} }

    override fun hasSuperType(vararg names: TypeNameDeclaration): Boolean = names.any {
        superclass.isSameAs(it) ||
        _superinterfaces.any { superInterface -> superInterface.type.isSameAs(it) }
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

    override fun addSuperinterface(superinterface: SuperInterfaceDeclaration) {
        _superinterfaces.add(superinterface)
    }

    override fun addSuperinterface(superinterface: Pair<TypeName, CodeBlock?>) = addSuperinterface(superinterface.toDeclaration())

    override fun addSuperinterface(typeName: TypeNameDeclaration, delegate: CodeDeclaration?) {
        _superinterfaces.add(SuperInterfaceDeclaration.fromSuperInterface(typeName, delegate))
    }

    override fun addSuperinterface(typeName: TypeName, delegate: CodeBlock?) {
        _superinterfaces.add(SuperInterfaceDeclaration.fromSuperInterface(typeName, delegate))
    }

    override fun addTypeVariable(typeVariable: TypeVariableNameDeclaration) {
        _typeVariables.add(typeVariable)
    }

    override fun addTypeVariable(typeVariable: TypeVariableName) = addTypeVariable(typeVariable.toDeclaration())

    override fun addFunction(function: FunctionDeclaration) {
        _functions.add(function)
    }

    override fun addFunction(function: FunSpec) = addFunction(function.toDeclaration())

    override fun addProperty(property: PropertyDeclaration) {
        _properties.add(property)
    }

    override fun addProperty(property: PropertySpec) = addProperty(property.toDeclaration())

    override fun addClass(klass: ClassDeclaration) {
        _classes.add(klass)
    }

    override fun addClass(klass: TypeSpec) = addClass(klass.toDeclaration())

    override fun addConstructor(constructor: FunctionDeclaration) {
        if (primaryConstructor == null) _primaryConstructor = constructor
        else _secondaryConstructors.add(constructor)
    }

    override fun addConstructor(constructor: FunSpec) = addConstructor(constructor.toDeclaration())

    override fun removeSuperinterface(superinterface: SuperInterfaceDeclaration) =
        _superinterfaces.removeFirst { superinterface.isSameAs(it) }

    override fun removeSuperinterface(superinterface: Pair<TypeName, CodeBlock?>) =
        removeSuperinterface(superinterface.toDeclaration())

    override fun removeSuperinterface(typeName: TypeNameDeclaration, delegate: CodeDeclaration?)
    = _superinterfaces.removeFirst { it.type.isSameAs(typeName) }

    override fun removeSuperinterface(typeName: TypeName, delegate: CodeBlock?) =
        _superinterfaces.removeFirst { it.type.isSameAs(typeName) }

    override fun removeTypeVariable(typeVariable: TypeVariableNameDeclaration) =
        _typeVariables.removeFirst { it.isSameAs(typeVariable) }

    override fun removeTypeVariable(typeVariable: TypeVariableName) =
        _typeVariables.removeFirst { it.isSameAs(typeVariable) }

    override fun removeFunction(function: FunctionDeclaration) =
        _functions.removeFirst { it.isSameAs(function) }

    override fun removeFunction(function: FunSpec) =
        _functions.removeFirst { it.isSameAs(function) }

    override fun removeProperty(property: PropertyDeclaration) =
        _properties.removeFirst { it.isSameAs(property) }

    override fun removeProperty(property: PropertySpec) =
        _properties.removeFirst { it.isSameAs(property) }

    override fun removeClass(klass: ClassDeclaration) =
        _classes.removeFirst { it.isSameAs(klass) }

    override fun removeClass(klass: TypeSpec) =
        _classes.removeFirst { it.isSameAs(klass) }

    override fun removeConstructor(constructor: FunctionDeclaration) {
        if (primaryConstructor == constructor) _primaryConstructor = null
        else  _secondaryConstructors.removeFirst { it.isSameAs(constructor) }
    }

    override fun removeConstructor(constructor: FunSpec) {
        if (primaryConstructor?.isSameAs(constructor) == true) _primaryConstructor = null
        else _secondaryConstructors.removeFirst { it.isSameAs(constructor) }
    }

    override fun isSameAs(other: ClassDeclaration): Boolean = (other as ClassDeclarationImpl).let { declaration ->
        _name == declaration.name &&
        _kind == declaration.kind &&
        _superclass.isSameAs(declaration.superclass) &&
        _superinterfaces.isSameAs(declaration.superinterfaces, SuperInterfaceDeclaration::isSameAs) &&
        _typeVariables.isSameAs(declaration.typeVariables, TypeVariableNameDeclaration::isSameAs) &&
        _functions.isSameAs(declaration.functions, FunctionDeclaration::isSameAs) &&
        _properties.isSameAs(declaration.properties, PropertyDeclaration::isSameAs) &&
        _classes.isSameAs(declaration.classes, ClassDeclaration::isSameAs) &&
        _annotations.isSameAs(declaration.annotations, AnnotationDeclaration::isSameAs) &&
        _modifiers.isSameAs(declaration.modifiers, KModifier::equals) &&
        _primaryConstructor.isSameAs(declaration.primaryConstructor, FunctionDeclaration::isSameAs) &&
        _secondaryConstructors.isSameAs(declaration.secondaryConstructors, FunctionDeclaration::isSameAs) &&
        _companionObject.isSameAs(declaration.companionObject, ClassDeclaration::isSameAs)
    }

    override fun isSameAs(other: TypeSpec): Boolean =
        _name == other.name &&
        _kind == other.kind &&
        _superclass.isSameAs(other.superclass) &&
        _superinterfaces.isSameAs(other.superinterfaces.entries, SuperInterfaceDeclaration::isSameAs) &&
        _typeVariables.isSameAs(other.typeVariables, TypeVariableNameDeclaration::isSameAs) &&
        _functions.isSameAs(other.funSpecs.filterNot(FunSpec::isConstructor), FunctionDeclaration::isSameAs) &&
        _properties.isSameAs(other.propertySpecs, PropertyDeclaration::isSameAs) &&
        _classes.isSameAs(other.typeSpecs.filterNot(TypeSpec::isCompanion), ClassDeclaration::isSameAs) &&
        _annotations.isSameAs(other.annotationSpecs, AnnotationDeclaration::isSameAs) &&
        _modifiers.isSameAs(other.modifiers, KModifier::equals) &&
        _primaryConstructor.isSameAs(other.primaryConstructor, FunctionDeclaration::isSameAs) &&
        _secondaryConstructors.isSameAs(other.funSpecs.filter(FunSpec::isConstructor), FunctionDeclaration::isSameAs) &&
        _companionObject.isSameAs(other.typeSpecs.firstOrNull(TypeSpec::isCompanion), ClassDeclaration::isSameAs)


    private var _typeSpec: TypeSpec? = null

    override fun rename(caller: String, name: String) {
        _thisClassName.rename(caller, name)
        super.rename(caller, name)

    }

    override fun lockRenaming() {
        (_thisClassName as ClassNameDeclarationImpl).lockRenaming()
        super.lockRenaming()
    }

    override fun toTypeSpec(): TypeSpec {
        if (_typeSpec == null) _typeSpec = when (_kind) {
            TypeSpec.Kind.CLASS -> if (_name == "<anonymous>") TypeSpec.anonymousClassBuilder() else TypeSpec.classBuilder(_name)
            TypeSpec.Kind.OBJECT -> if (isCompanion) TypeSpec.companionObjectBuilder() else TypeSpec.objectBuilder(_name)
            TypeSpec.Kind.INTERFACE -> TypeSpec.interfaceBuilder(_name)
        }
            .applyIf(kind != TypeSpec.Kind.INTERFACE) { superclass(_superclass.toTypeName()) }
            .addIfNotEmpty(_modifiers, TypeSpec.Builder::addModifiers)
            .addIfNotNull(_companionObject, ClassDeclaration::toTypeSpec, TypeSpec.Builder::addType)
            .addIfNotNull(_primaryConstructor, FunctionDeclaration::toFunSpec, TypeSpec.Builder::primaryConstructor)
            .addFolding(_secondaryConstructors, FunctionDeclaration::toFunSpec, TypeSpec.Builder::addFunction)
            .addFolding(_typeVariables, TypeVariableNameDeclaration::toTypeName, TypeSpec.Builder::addTypeVariable)
            .addFolding(_annotations, AnnotationDeclaration::toAnnotationSpec, TypeSpec.Builder::addAnnotation)
            .addFolding(_superinterfaces, SuperInterfaceDeclaration::toSuperInterface) { (typeName, delegate) ->
                if (delegate == null) addSuperinterface(typeName)
                else addSuperinterface(typeName, delegate)
            }
            .addFolding(_properties, PropertyDeclaration::toPropertySpec, TypeSpec.Builder::addProperty)
            .addFolding(_functions, FunctionDeclaration::toFunSpec, TypeSpec.Builder::addFunction)
            .addFolding(_classes, ClassDeclaration::toTypeSpec, TypeSpec.Builder::addType)
            .build()
        return _typeSpec!!
    }

    override fun refresh() {
        _typeSpec = null
        updateChildren()
        members.forEach(Declaration::refresh)
    }

    companion object {
        fun fromTypeSpec(typeSpec: TypeSpec): ClassDeclaration = ClassDeclarationImpl(
            name = typeSpec.name ?: "<anonymous>",
            kind = typeSpec.kind,
            superclass = typeSpec.superclass.toDeclaration(),
            superinterfaces = typeSpec.superinterfaces.map(Map.Entry<TypeName, CodeBlock?>::toDeclaration),
            typeVariables = typeSpec.typeVariables.map(TypeVariableName::toDeclaration),
            constructor = typeSpec.primaryConstructor?.let(FunSpec::toDeclaration),
            secondaryConstructors = typeSpec.funSpecs.filter(FunSpec::isConstructor).map(FunSpec::toDeclaration),
            functions = typeSpec.funSpecs.filterNot(FunSpec::isConstructor).map(FunSpec::toDeclaration),
            properties = typeSpec.propertySpecs.map(PropertySpec::toDeclaration),
            classes = typeSpec.typeSpecs.filterNot(TypeSpec::isCompanion).map(TypeSpec::toDeclaration),
            annotations = typeSpec.annotationSpecs.map(AnnotationSpec::toDeclaration),
            modifiers = typeSpec.modifiers,
            companionObject = typeSpec.typeSpecs.firstOrNull(TypeSpec::isCompanion)?.toDeclaration()
        )

        private val unrealEnumClassNames: Set<String> = UnrealEnumCorrections.unrealEnumTypeNames

    }

}