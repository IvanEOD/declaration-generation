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
    override fun correct(files: Set<FileDeclaration>) {
        val allMembers = files.flatMap { it.members }
        val allProperties = allMembers.filterIsInstance<PropertyDeclaration>()
        val allFunctions = allMembers.filterIsInstance<FunctionDeclaration>()
        val allTypeAliases = allMembers.filterIsInstance<TypeAliasDeclaration>()

        allTypeAliases.forEach {
            if (it.name == "timeout_handle") it.rename("TimeoutHandle")
            it.lockRenaming()
            environment.addFileNonClass(it)
        }

        allFunctions.forEach {
            if (it.name == "setTimeout") {
                it.parameters.forEach { parameter ->
                    if (parameter.name == "fn") {
                        parameter.rename("function")
                        parameter.lockRenaming()
                    }
                }
            }
            it.lockRenaming()
            environment.addFileNonClass(it)
        }

        allProperties.forEach {
            if (it.name == "process") it.rename("GProcess")
            if (it.name == "memory") it.rename("GMemory")
            if (it.name != "Root" && it.type.toString() != "dynamic") {
                environment.addFileNonClass(it)
            }
            it.lockRenaming()
        }

    }
}