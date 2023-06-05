package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.LambdaTypeName


/**
 *  Lambda Type Name Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 5:38 PM EST )
 */

sealed interface LambdaTypeNameDeclaration : TypeNameDeclaration {
    val annotations: Set<AnnotationDeclaration>
    val parameters: Set<ParameterDeclaration>
    val receiver: TypeNameDeclaration?
    val returnType: TypeNameDeclaration
    val suspending: Boolean
    val nullable: Boolean

    fun isSameAs(other: LambdaTypeNameDeclaration): Boolean
    fun isSameAs(other: LambdaTypeName): Boolean

    override fun toTypeName(): LambdaTypeName

    companion object {
        fun fromLambdaTypeName(lambdaTypeName: LambdaTypeName): LambdaTypeNameDeclaration =
            LambdaTypeNameDeclarationImpl.fromLambdaTypeName(lambdaTypeName)
    }

}



