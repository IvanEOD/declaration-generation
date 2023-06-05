package com.detpros.unrealkotlin.utility


/**
 *  Rename Data
 *
 * @author IvanEOD ( 5/23/2023 at 6:44 PM EST )
 */
class RenameData {
    private val renameMap = mutableMapOf<String, String>()

    val keys get() = renameMap.keys
    val values get() = renameMap.values

    operator fun set(from: String, to: String) {
        renameMap[from] = to
    }

    operator fun get(from: String): String {
        var lastValidName = from
        var name: String? = lastValidName
        while (name != null) {
            lastValidName = name
            name = renameMap[name]
        }
        return lastValidName
    }

    fun forEach(action: (String, String) -> Unit) {
        renameMap.forEach(action)
    }

}