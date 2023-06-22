package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.corrections.models.*
import com.detpros.unrealkotlin.declaration.*
import com.detpros.unrealkotlin.utility.toMemberLevel
import com.detpros.unrealkotlin.utility.toTopLevel
import com.squareup.kotlinpoet.KModifier

/**
 *  Unreal Standard Corrections
 *
 * @author IvanEOD ( 5/26/2023 at 3:02 PM EST )
 */
class UnrealStandardCorrections(
    override val environment: CorrectionEnvironment,
    private val configuration: StandardDeclarationsCorrection,
) : Corrections() {
    private val processed = mutableSetOf<Declaration>()
    private fun isProcessed(declaration: Declaration) = !processed.add(declaration) || environment.isIgnore(declaration)

    private val definedClassRenames by lazy { configuration.definedClassRenames() }
    private val addOverrides by lazy { configuration.getAddOverrides() }
    private val ignoreFunctionNames by lazy { configuration.ignoreFunctionNames() }
    private val ignorePropertyNames by lazy { configuration.ignorePropertyNames() }
    private val commonPrefixRenames by lazy { configuration.commonPrefixReplacements() }
    private val definedMemberRenames by lazy { configuration.definedMemberRenames() }
    private val renameMemberProperties by lazy { configuration.definedPropertyRenames() }
    private val renameMemberFunctions by lazy { configuration.definedFunctionRenames() }

    fun functionConfig(name: String) = configuration.allMemberFunctions().find { it.name == name }
    fun propertyConfig(name: String) = configuration.allMemberProperties().find { it.name == name }
    fun classConfig(name: String) = configuration.classConfigurations().find { it.name == name }
    fun functionConfig(declaration: FunctionDeclaration) = functionConfig(declaration.originalName)
    fun propertyConfig(declaration: PropertyDeclaration) = propertyConfig(declaration.originalName)
    fun classConfig(declaration: ClassDeclaration) = classConfig(declaration.originalName)

    fun internalCorrect(parent: Declaration? = null, declarations: Set<Declaration>) {
        declarations.forEach {
            when (it) {
                is FileDeclaration -> correctFile(it)
                is ClassDeclaration -> correctClass(it)
                is FunctionDeclaration -> correctFunction(parent, it)
                is PropertyDeclaration -> correctProperty(parent, it)
                is TypeAliasDeclaration -> correctTypeAlias(it)
                else -> {}
            }
        }
    }

    override fun correct(files: Set<FileDeclaration>) {
        files.forEach { correctFile(it) }
    }

    private fun correctFile(file: FileDeclaration) {
        if (isProcessed(file)) return
        internalCorrect(file, file.members)
    }

    private fun correctClass(klass: ClassDeclaration) {
        if (isProcessed(klass)) return
        with(environment) {
            if (isIgnore(klass)) return
            with(klass) {
                val newName = definedClassRenames[name] ?: name.toTopLevel()
                if (name != newName) rename("standardClassCorrections", newName)
                val config = classConfig(this)
                config?.correct(klass)
                addFinishedClass(this)
                members.forEach membersForEach@ { member ->
                    when (member) {
                        is ClassDeclarationImpl -> correctClass(member)
                        is FunctionDeclarationImpl -> correctFunction(klass, member)
                        is PropertyDeclarationImpl -> correctProperty(klass, member)
                        else -> {}
                    }
                }
                if (hasSuperType("MediaSource")) {
                    functions.forEach {
                        it.removeTypeVariables()
                        it.removeModifier(KModifier.OVERRIDE)
                    }
                }

            }
        }
    }

    private fun correctFunction(parent: Declaration? = null, declaration: FunctionDeclaration) {
        if (isProcessed(declaration)) return
        if (environment.isIgnore(declaration)) return
        if (declaration.name in ignoreFunctionNames) return
        val renameMap = mutableMapOf<String, String>()
        if (parent != null && parent is ClassDeclaration) {
            val definedPropertyRename = definedMemberRenames[parent.name]
            if (definedPropertyRename != null) renameMap.putAll(definedPropertyRename)

            val overrides = addOverrides[parent.name] ?: emptySet()
            if (declaration.name in overrides) {
                declaration.addModifier(KModifier.OVERRIDE)
                declaration.removeJsName()
            }

        }
        var rename = renameMap[declaration.name] ?: renameMemberFunctions[declaration.name]
        if (rename == "new" && parent != null && parent is ClassDeclaration && !parent.isCompanion) rename = "C"
        if (rename != null) declaration.rename("standardClassFunctionCorrections", rename)
        else {
            val newName = declaration.name.replaceAnyCommonPrefixes().toMemberLevel()
            declaration.rename("standardClassFunctionElseCorrections", newName)
        }
        functionConfig(declaration)?.correct(declaration)
        declaration.lockRenaming()
        declaration.members.filterIsInstance<ParameterDeclaration>().forEach {
            val parameterName = it.name.toMemberLevel()
            it.rename("standardClassFunctionParameterCorrections", parameterName)
            it.lockRenaming()
        }
    }

    private fun correctProperty(parent: Declaration? = null, declaration: PropertyDeclaration) {
        if (isProcessed(declaration)) return
        if (environment.isIgnore(declaration)) return
        val renameMap = mutableMapOf<String, String>()
        if (parent != null && parent is ClassDeclaration) {
            val definedPropertyRename = definedMemberRenames[parent.name]
            if (definedPropertyRename != null) renameMap.putAll(definedPropertyRename)
            val overrides = addOverrides[parent.name] ?: emptySet()
            if (declaration.name in overrides) {
                declaration.addModifier(KModifier.OVERRIDE)
                declaration.removeJsName()
            }
        }
        if (declaration.name !in ignorePropertyNames) {
            val rename = renameMap[declaration.name] ?: renameMemberProperties[declaration.name]
            if (rename != null) declaration.rename("standardClassPropertyCorrections", rename)
            else {
                val newName = declaration.name.replaceAnyCommonPrefixes().toMemberLevel()
                declaration.rename("standardClassPropertyElseCorrections", newName)
            }
        }
        propertyConfig(declaration)?.correct(declaration)
        declaration.lockRenaming()

    }

    private fun correctTypeAlias(declaration: TypeAliasDeclaration) {
        if (environment.isIgnore(declaration)) return
        val newName = declaration.name.toMemberLevel()
        if (declaration.name != newName && !environment.isIgnore(declaration)) declaration.rename("standardTypeAliasCorrections", newName)
        declaration.lockRenaming()
    }

    private fun String.replaceAnyCommonPrefixes(): String =
        commonPrefixRenames.entries.fold(this) { acc, (prefix, replacement) ->
            if (acc.startsWith(prefix)) acc.replaceFirst(prefix, replacement) else acc
        }

    companion object {
        private val defaults by lazy { StandardDeclarationsCorrection.Default }
    }

}