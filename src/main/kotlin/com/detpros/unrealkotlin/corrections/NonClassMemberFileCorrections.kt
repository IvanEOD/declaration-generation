package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.declaration.FileDeclaration
import com.detpros.unrealkotlin.declaration.FunctionDeclaration
import com.detpros.unrealkotlin.declaration.PropertyDeclaration
import com.detpros.unrealkotlin.declaration.TypeAliasDeclaration


/**
 *  Non Class Member File Corrections
 *
 * @author IvanEOD ( 5/26/2023 at 3:13 PM EST )
 */
class NonClassMemberFileCorrections(
    override val environment: CorrectionEnvironment,
    private val configuration: NonClassMemberCorrectionsConfiguration,
) : Corrections() {

    private val typeAliasRenames by lazy { configuration.typeAliasRenames() }
    private val propertyRenames by lazy { configuration.propertyRenames() }

    override fun correct(files: Set<FileDeclaration>) {
        val allMembers = files.flatMap { it.members }
        val allProperties = allMembers.filterIsInstance<PropertyDeclaration>()
        val allFunctions = allMembers.filterIsInstance<FunctionDeclaration>()
        val allTypeAliases = allMembers.filterIsInstance<TypeAliasDeclaration>()

        allTypeAliases.forEach {
            val newName = typeAliasRenames[it.name]
            if (newName != null) it.rename("allTypeAliasesRename", newName)
//            if (it.name == "timeout_handle") it.rename("TimeoutHandle")
            it.lockRenaming()
            environment.addFileNonClass(it)
        }

        allFunctions.forEach {
            if (it.name == "setTimeout") {
                it.parameters.forEach { parameter ->
                    if (parameter.name == "fn") {
                        parameter.rename("allFunctionsRename", "function")
                        parameter.lockRenaming()
                    }
                }
            }
            it.lockRenaming()
            environment.addFileNonClass(it)
        }

        allProperties.forEach {
            val newName = propertyRenames[it.name]
            if (newName != null) it.rename("allPropertiesRename", newName)
//            if (it.name == "process") it.rename("GProcess")
//            if (it.name == "memory") it.rename("GMemory")
            if (it.name != "Root" && it.type.toString() != "dynamic") environment.addFileNonClass(it)
            it.lockRenaming()
        }

    }

    companion object {
        private val defaults by lazy { NonClassMemberCorrectionsConfiguration.Default }
    }

}