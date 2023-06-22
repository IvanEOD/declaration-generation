package com.detpros.unrealkotlin.corrections.models

import com.detpros.unrealkotlin.declaration.DeclarationWithName
import com.detpros.unrealkotlin.declaration.FunctionDeclaration
import com.detpros.unrealkotlin.declaration.PropertyDeclaration


/**
 *  Correction With Members
 *
 * @author IvanEOD ( 6/22/2023 at 12:51 PM EST )
 */
interface CorrectionWithMembers<T : CorrectionWithMembers<T, B>, B : CorrectionWithMembers.Builder<T, B>> : Correction<T, B> {

    val members: List<MemberCorrection>
    val functions: List<FunctionCorrection>
    val properties: List<PropertyCorrection>

    fun functionConfig(name: String): FunctionCorrection? = functions.find { it.name == name }
    fun propertyConfig(name: String, type: String? = null): PropertyCorrection? = properties.find { it.name == name && (type == null || it.type == type) }
    fun memberConfig(name: String): MemberCorrection? = members.find { it.name == name }
    fun functionConfig(declaration: FunctionDeclaration) = functionConfig(declaration.originalName)
    fun propertyConfig(declaration: PropertyDeclaration) = propertyConfig(declaration.originalName)
    fun memberConfig(declaration: DeclarationWithName) = memberConfig(declaration.originalName)


    interface Builder<T : Correction<T, B>, B : Builder<T, B>>: Correction.Builder<T, B> { fun memberConfiguration(configuration: MemberCorrection.Builder): B

        fun functionConfiguration(configuration: FunctionCorrection.Builder): B
        fun propertyConfiguration(configuration: PropertyCorrection.Builder): B
        fun renameMember(name: String, newName: String) = memberConfiguration(MemberCorrection.Builder(name, newName))

        fun property(
            name: String,
            type: String? = null,
            block: PropertyCorrection.Builder.() -> Unit
        ) = propertyConfiguration(PropertyCorrection.Builder(name, type = type).apply(block))

        fun function(
            name: String,
            returnType: String? = null,
            block: FunctionCorrection.Builder.() -> Unit
        ) = functionConfiguration(FunctionCorrection.Builder(name, returnType = returnType).apply(block))

        fun renameProperty(
            name: String,
            newName: String,
            type: String? = null
        ) = property(name, type) { rename(newName) }

        fun renameFunction(
            name: String,
            newName: String,
            returnType: String? = null
        ) = function(name, returnType) { rename(newName) }



    }


}