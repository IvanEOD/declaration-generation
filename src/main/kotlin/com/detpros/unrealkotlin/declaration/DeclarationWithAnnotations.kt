package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.AnnotationSpec


/**
 *  Declaration With Annotations
 *
 * @author IvanEOD ( 5/24/2023 at 11:02 AM EST )
 */
sealed interface DeclarationWithAnnotations : Declaration {
    val annotations: Set<AnnotationDeclaration>

    fun addAnnotation(annotation: AnnotationDeclaration)
    fun addAnnotation(annotation: AnnotationSpec) {
        addAnnotation(annotation.toDeclaration())
    }
    fun removeAnnotation(annotation: AnnotationDeclaration)
    fun removeAnnotation(annotation: AnnotationSpec) {
        removeAnnotation(annotation.toDeclaration())
    }
    fun clearAnnotations()
}

