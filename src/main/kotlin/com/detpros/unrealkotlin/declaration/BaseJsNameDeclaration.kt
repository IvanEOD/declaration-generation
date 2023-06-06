package com.detpros.unrealkotlin.declaration


/**
 *  Base Js Name Declaration
 *
 * @author IvanEOD ( 5/24/2023 at 2:59 PM EST )
 */

internal sealed class BaseJsNameDeclaration : ParentDeclaration(), DeclarationWithJsName {
    private var _lockedRenaming = false
    protected var _name: String = ""
    protected val _annotations = mutableSetOf<AnnotationDeclaration>()

    override fun onChildClaimed(declaration: ChildDeclaration) {
    }

    override fun onChildReleased(declaration: ChildDeclaration) {
    }

    private var _jsName: String
        get() = annotations.find(AnnotationDeclaration::isJsName)?.getJsName() ?: ""
        set(value) {
            setJsName(value)
        }
    override val jsName: String get() = _jsName

    override fun removeJsName() {
        _annotations.removeIf(AnnotationDeclaration::isJsName)
    }

    override fun setJsName(name: String) {
        if (isJsNamePresent) removeJsName()
        if (isJsNameAllowed) _annotations.add(AnnotationDeclaration.jsName(name))
    }

    override fun rename(caller: String, name: String) {
        if (isRenamingLocked) return
        val oldName = _name
        _name = name
        if (isJsNameAllowed && name != oldName) setJsName(originalName)
        when (this) {
            is TypeAliasDeclaration,
            is ClassDeclaration -> ClassNameDeclaration.renameClass(oldName, name)
            else -> {}
        }
    }

    override fun lockRenaming() {
        _lockedRenaming = true
    }

    override fun unlockRenaming() {
        _lockedRenaming = false
    }

    override val isRenamingLocked: Boolean get() = _lockedRenaming

}