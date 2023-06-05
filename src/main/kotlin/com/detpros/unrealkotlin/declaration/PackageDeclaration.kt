package com.detpros.unrealkotlin.declaration

import java.io.File


/**
 *  Package Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:44 PM EST )
 */
sealed interface PackageDeclaration : Declaration {

    val name: String
    val files: Set<FileDeclaration>
    fun isSameAs(other: PackageDeclaration): Boolean

    fun addFile(file: FileDeclaration)
    fun removeFile(file: FileDeclaration)

    fun write(destination: File)

    companion object {
        fun withSources(name: String, files: Set<FileDeclaration> = emptySet()): PackageDeclaration = PackageDeclarationImpl(name, files)
    }

}

