package com.detpros.unrealkotlin.corrections.models

import com.detpros.unrealkotlin.declaration.ClassDeclaration


/**
 *  Class Corrections Provider
 *
 * @author IvanEOD ( 6/22/2023 at 12:22 PM EST )
 */

interface ClassCorrectionsProvider {
    fun classConfigurations(): List<ClassCorrection>

    fun definedClassRenames() = classConfigurations()
        .filter { it.newName != null }
        .associate { it.name to it.newName!! }

    fun definedMemberRenames(): Map<String, Map<String, String>> = classConfigurations()
        .filter { it.members.isNotEmpty() && it.members.any { member -> member.newName != null } }
        .associate {
            it.name to it.members
                .associate { member -> member.name to member.newName!! }
        }

    fun classConfig(name: String): ClassCorrection? = classConfigurations().find { it.name == name }
    fun classConfig(declaration: ClassDeclaration) = classConfig(declaration.originalName)

    operator fun get(className: String): ClassCorrection? =
        classConfigurations().find { it.name == className }

}
