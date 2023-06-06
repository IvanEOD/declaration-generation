package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName


/**
 *  Class Name Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 5:39 PM EST )
 */

sealed interface ClassNameDeclaration : TypeNameDeclaration, DeclarationWithAnnotations {

    val nullable: Boolean
    val packageName: String
    val simpleNames: List<String>
    val simpleName: String

    fun rename(caller: String, name: String)
    fun setPackage(name: String)

    fun isSameAs(other: ClassNameDeclaration): Boolean
    fun isSameAs(other: ClassName): Boolean

    override fun toTypeName(): ClassName

    companion object {
        private val main = ClassNameDeclarationImpl.Companion

        fun renameClass(oldName: String, newName: String) = main.renameClass(oldName, newName)
        fun getUpdatedName(name: String): String = main.getUpdatedName(name)
        fun getClassName(from: String): ClassNameDeclaration = main.getClassName(from)
        fun setClassNamePackage(className: String, packageName: String) = main.setClassNamePackage(className, packageName)
        fun setPackageToUE(className: String) = main.setPackageToUE(className)
        fun fromClassName(className: ClassName): ClassNameDeclaration = main.fromClassName(className)
        fun fromTypeName(typeName: TypeName): ClassNameDeclaration = main.fromTypeName(typeName)
        fun forEach(action: (ClassNameDeclaration) -> Unit) = main.forEach(action)

        init {
            main
        }

    }


}

