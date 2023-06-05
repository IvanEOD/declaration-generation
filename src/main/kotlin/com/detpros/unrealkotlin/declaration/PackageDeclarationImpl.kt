package com.detpros.unrealkotlin.declaration

import com.detpros.unrealkotlin.utility.isSameAs
import java.io.File


/**
 *  Package Declaration Impl
 *
 * @author IvanEOD ( 5/24/2023 at 11:00 AM EST )
 */
internal class PackageDeclarationImpl(
    name: String,
    files: Set<FileDeclaration> = emptySet(),
): ParentDeclaration(), PackageDeclaration {

    private var _name: String = name
    override val name: String get() = _name
    private val _files = files.toMutableSet()
    override val files: Set<FileDeclaration> get() = _files
    override val members: Set<Declaration> get() = _files.toSet()

    override fun onChildClaimed(declaration: ChildDeclaration) {
        val previousParent = declaration._parent
        previousParent?.onChildReleased(declaration)
        declaration._parent = this
    }

    override fun onChildReleased(declaration: ChildDeclaration) {
        declaration._parent = null
    }

    override fun refresh() {
        updateChildren()
        _files.forEach { it.refresh() }
    }

    override fun addFile(file: FileDeclaration) {
        _files.add(file)
        file as FileDeclarationImpl
        file._packageName = name
    }

    override fun removeFile(file: FileDeclaration) {
        _files.remove(file)
        file as FileDeclarationImpl
        file._packageName = ""
    }

    override fun isSameAs(other: PackageDeclaration): Boolean =
        _files.isSameAs(other.files, FileDeclaration::isSameAs)

    override fun write(destination: File) {
        _files.forEach { it.write(destination) }
    }
}