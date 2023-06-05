package com.detpros.unrealkotlin.utility

data class ClassDependencies(val name: String, val dependencies: Set<String>)
data class ManagedDependencies(val dependencies: Set<ClassDependencies>) {

    fun getDependencies(name: String): Set<String> = dependencies.find { it.name == name }?.dependencies ?: emptySet()

    fun getAllDependencies(name: String): Set<String> = getAllDependencies(setOf(name))

    fun getAllDependencies(names: Set<String>): Set<String> = getAllDependencies(emptySet(), names)

    private fun getAllDependencies(
        checked: Set<String>,
        unchecked: Set<String>,
        names: Set<String> = emptySet()
    ): Set<String> {
        val newChecked = checked + unchecked
        val newUnchecked = unchecked.flatMap { getDependencies(it) }.toSet() - newChecked
        val newNames = names + unchecked
        return if (newUnchecked.isEmpty()) newNames else getAllDependencies(newChecked, newUnchecked, newNames)
    }


}

