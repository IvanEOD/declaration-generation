package com.detpros.unrealkotlin.declaration

import com.detpros.unrealkotlin.utility.RenameData
import com.squareup.kotlinpoet.*


/**
 *  Class Name Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 10:59 AM EST )
 */

internal class ClassNameDeclarationImpl(
    packageName: String,
    simpleNames: List<String>,
    annotations: Set<AnnotationDeclaration> = emptySet(),
    nullable: Boolean = false,
    private var renameable: Boolean = true,
): ClassNameDeclaration {
    private var _packageName: String = packageName
    private var _simpleNames = simpleNames.toList()
    private val _annotations = annotations.toMutableSet()
    private val _nullable = nullable
    override val members: Set<Declaration> get() = annotations
    override val annotations: Set<AnnotationDeclaration> get() = _annotations
    override val packageName: String get() = _packageName
    override val simpleNames: List<String> get() = _simpleNames
    override val nullable: Boolean get() = _nullable
    override val simpleName get() = simpleNames.last()

    override fun isName(name: String): Boolean = simpleNames.last() == name || simpleNames.joinToString(".") == name
    override fun toString(): String = toTypeName().toString()

    override fun isSameAs(typeName: TypeName): Boolean = if (typeName is ClassName) isSameAs(typeName) else false
    override fun isSameAs(other: TypeNameDeclaration): Boolean = other is ClassNameDeclaration && isSameAs(other)
    override fun isSameAs(other: ClassNameDeclaration): Boolean = other.simpleNames == simpleNames && other.packageName == packageName
    override fun isSameAs(other: ClassName): Boolean = other.simpleNames == simpleNames && other.packageName == packageName

    private var _className: ClassName? = null

    override fun allNames(): List<String> = listOf(_packageName) + _simpleNames
    override fun toTypeName(): ClassName {
        if (_className == null) {
            val name = _simpleNames.last()
            val finalName = getUpdatedName(name)
            if (finalName != name) rename(finalName)
            _className = ClassName(_packageName, _simpleNames).copy(
                nullable = nullable,
                annotations = annotations.map(AnnotationDeclaration::toAnnotationSpec),
                tags = emptyMap()
            )
        }
        return _className!!
    }

    override fun refresh() {
        _className = null
        members.forEach(Declaration::refresh)
    }

    fun lockRenaming() {
        renameable = false
    }

    override fun rename(name: String) {
        if (!renameable) return
        val oldName = simpleNames.last()
        if (name == oldName) return
        _simpleNames = _simpleNames.dropLast(1) + name
    }

    override fun setPackage(name: String) {
        if (!renameable) return
        if (_packageName == name) return
        _packageName = name
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

    companion object {
        private val renameData = RenameData()
        private val classNames = mutableMapOf<ClassName, ClassNameDeclaration>()

        fun renameClass(oldName: String, newName: String, lock: Boolean = false) {
            renameData[oldName] = newName
            classNames.values.asSequence().filter { it.simpleName == oldName }.forEach {
                it.rename(newName)
                if (lock) (it as ClassNameDeclarationImpl).lockRenaming()
            }
        }

        fun getUpdatedName(name: String): String = renameData[name]
        fun getClassName(from: String): ClassNameDeclaration =
            classNames.values.find { it.simpleName == from } ?: fromClassName(ClassName.bestGuess(from))

        fun setClassNamePackage(className: String, packageName: String) {
            val declaration = getClassName(className)
            declaration.setPackage(packageName)
        }
        fun setPackageToUE(className: String) = setClassNamePackage(className, "ue")

        fun fromClassName(className: ClassName): ClassNameDeclaration = ClassNameDeclarationImpl(
            className.packageName,
            className.simpleNames,
            className.annotations.map(AnnotationSpec::toDeclaration).toSet(),
            className.isNullable,
        )
        private fun privateFromClassName(className: ClassName): ClassNameDeclaration = ClassNameDeclarationImpl(
            className.packageName,
            className.simpleNames,
            className.annotations.map(AnnotationSpec::toDeclaration).toSet(),
            className.isNullable,
            false
        )
        fun fromTypeName(typeName: TypeName): ClassNameDeclaration = when (typeName) {
            is ClassName -> fromClassName(typeName)
            else -> throw IllegalArgumentException("Cannot create ClassNameDeclaration from $typeName (${typeName::class.simpleName}")
        }
        private fun setDefault(className: ClassName): ClassNameDeclaration =
            classNames.getOrPut(className) { privateFromClassName(className) }

        fun forEach(action: (ClassNameDeclaration) -> Unit) = classNames.values.forEach(action)

        private val typeNames = setOf(
            ANY,
            ARRAY,
            UNIT,
            BOOLEAN,
            BYTE,
            SHORT,
            INT,
            LONG,
            CHAR,
            FLOAT,
            DOUBLE,
            STRING,
            CHAR_SEQUENCE,
            COMPARABLE,
            THROWABLE,
            ANNOTATION,
            NOTHING,
            NUMBER,
            ITERABLE,
            COLLECTION,
            LIST,
            SET,
            MAP,
            MAP_ENTRY,
            MUTABLE_ITERABLE,
            MUTABLE_COLLECTION,
            MUTABLE_LIST,
            MUTABLE_SET,
            MUTABLE_MAP,
            MUTABLE_MAP_ENTRY,
            BOOLEAN_ARRAY,
            BYTE_ARRAY,
            CHAR_ARRAY,
            SHORT_ARRAY,
            INT_ARRAY,
            LONG_ARRAY,
            FLOAT_ARRAY,
            DOUBLE_ARRAY,
            ENUM,
            U_BYTE,
            U_SHORT,
            U_INT,
            U_LONG,
            U_BYTE_ARRAY,
            U_SHORT_ARRAY,
            U_INT_ARRAY,
            U_LONG_ARRAY,
//            STAR,
//            DYNAMIC,
        )

        init {
            typeNames.forEach { setDefault(it) }
        }
    }


}
