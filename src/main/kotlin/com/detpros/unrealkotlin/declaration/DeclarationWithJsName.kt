package com.detpros.unrealkotlin.declaration


/**
 *  Js Named Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:45 PM EST )
 */
sealed interface DeclarationWithJsName : DeclarationWithName, DeclarationWithAnnotations {

    override val annotations: Set<AnnotationDeclaration>

    val jsName: String

    val isJsNameAllowed: Boolean get() = true
    val isJsNamePresent: Boolean get() = jsName.isNotEmpty()

    fun setJsName(name: String)
    fun removeJsName()

}

