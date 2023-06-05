package com.detpros.unrealkotlin.corrections


/**
 *  Defined Renames
 *
 * @author IvanEOD ( 6/5/2023 at 9:58 AM EST )
 */
data class DefinedRenames(
    val classRenames: Map<String, String> = emptyMap(),
    val classMemberRenames: Map<String, Map<String, String>> = emptyMap(),
) {

    operator fun plus(other: DefinedRenames): DefinedRenames {
        return DefinedRenames(
            classRenames = classRenames + other.classRenames,
            classMemberRenames = classMemberRenames + other.classMemberRenames,
        )
    }

    fun getClassRename(className: String): String? = classRenames[className]
    fun getClassMemberRename(className: String, memberName: String): String? = getClassMemberRenames(className)[memberName]
    fun getClassMemberRenames(className: String): Map<String, String> = classMemberRenames[className] ?: emptyMap()

    fun hasClassRename(className: String): Boolean = getClassRename(className)?.let { it != className && it.isNotEmpty() } ?: false
    fun hasClassMemberRename(className: String, memberName: String): Boolean = getClassMemberRename(className, memberName)?.let { it != memberName && it.isNotEmpty() } ?: false
    fun hasClassMemberRenames(className: String): Boolean = getClassMemberRenames(className).isNotEmpty()


    companion object {
        val empty = DefinedRenames()
    }




}