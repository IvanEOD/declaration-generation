package com.detpros.unrealkotlin.utility


/**
 *  Generics Class
 *
 * @author IvanEOD ( 5/26/2023 at 3:18 PM EST )
 */
internal data class GenericClass(val properties: Set<Pair<String, String>>) {
    override fun equals(other: Any?): Boolean {
        if (other !is GenericClass) return false
        return properties == other.properties
    }

    override fun hashCode(): Int = 31 + properties.hashCode()
}
