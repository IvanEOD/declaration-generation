package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.declaration.*
import com.detpros.unrealkotlin.utility.toMemberLevel
import com.detpros.unrealkotlin.utility.toTopLevel
import com.squareup.kotlinpoet.KModifier

/**
 *  Standard Corrections
 *
 * @author IvanEOD ( 5/26/2023 at 3:02 PM EST )
 */
class StandardCorrections(
    override val environment: CorrectionEnvironment,
    private val configuration: StandardCorrectionsConfiguration,
) : Corrections() {
    private val processed = mutableSetOf<Declaration>()
    private fun isProcessed(declaration: Declaration) = !processed.add(declaration) || environment.isIgnore(declaration)

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
                if (name != newName) rename(newName)
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
        if (rename != null) declaration.rename(rename)
        else {
            val newName = declaration.name.replaceAnyCommonPrefixes().toMemberLevel()
            declaration.rename(newName)
        }
        declaration.lockRenaming()
        declaration.members.filterIsInstance<ParameterDeclaration>().forEach {
            val parameterName = it.name.toMemberLevel()
            it.rename(parameterName)
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
            if (rename != null) declaration.rename(rename)
            else {
                val newName = declaration.name.replaceAnyCommonPrefixes().toMemberLevel()
                declaration.rename(newName)
            }
        }
        declaration.lockRenaming()

    }

    private fun correctTypeAlias(declaration: TypeAliasDeclaration) {
        if (environment.isIgnore(declaration)) return
        val newName = declaration.name.toMemberLevel()
        if (declaration.name != newName && !environment.isIgnore(declaration)) declaration.rename(newName)
        declaration.lockRenaming()
    }

    private fun String.replaceAnyCommonPrefixes(): String =
        commonPrefixRenames.entries.fold(this) { acc, (prefix, replacement) ->
            if (acc.startsWith(prefix)) acc.replaceFirst(prefix, replacement) else acc
        }

    companion object {

        val definedClassRenames = mapOf(
            "KotlinObject" to "InternalKotlinObject"
        )

        val addOverrides = mapOf(
            "NavigationEvent" to setOf("clone"),
            "UPointerEvent" to setOf("clone"),
            "KeyEvent" to setOf("clone"),
            "MotionEvent" to setOf("clone"),
            "CharacterEvent" to setOf("clone"),
            "InterchangeBaseNode" to setOf("GetUniqueID"),
            "TextBlock" to setOf("bAutoWrapText"),
        )

        val ignoreFunctionNames = mutableSetOf(
            "ToString"
        )
        val ignorePropertyNames = mutableSetOf<String>(

        )

        val renameMemberProperties = mutableMapOf(
            "$" to "value"
        )

        val renameMemberFunctions = mutableMapOf(
            "C" to "new"
        )

        val commonPrefixRenames = mapOf(
            "NotEqual_" to "notEqual",
            "Multiply_" to "multiply",
            "EqualEqual_" to "isEqualTo",
            "Add_" to "plus",
            "Conv_" to "convert",
            "Divide_" to "divide",
            "Subtract_" to "minus",
            "PointerEvent_" to "",
        )

        val logVisualizerSettings = mapOf(
            "bPersistentFilters" to "usePersistentFilters"
        )
        val lightComponentBase= mapOf(
            "bCastRaytracedShadow" to "isCastRaytracedShadow",
        )

        val definedMemberRenames = mapOf(
            "LogVisualizerSettings" to logVisualizerSettings,
            "LightComponentBase" to lightComponentBase,
            "StaticMeshLightingInfo" to mapOf("bTextureMapping" to "useTextureMapping"),
            "RendererSettings" to mapOf("bDefaultFeatureAutoExposure" to "useDefaultFeatureAutoExposure"),
            "NiagaraMeshRendererProperties" to mapOf("bOverrideMaterials" to "isOverrideMaterials"),
            "NiagaraSimulationStageGeneric" to mapOf("bOverrideGpuDispatchNumThreads" to "isOverrideGpuDispatchNumThreads"),
            "NiagaraEmitter" to mapOf("bFixedBounds" to "isFixedBounds"),
            "NiagaraSystem" to mapOf("bFixedBounds" to "isFixedBounds"),
            "AnimNodeLookAt" to mapOf("LookUpAxis" to "lookUpAxisString", "LookAtAxis" to "lookAtAxisString"),
            "AnimNodeRigidBody" to mapOf("bOverrideWorldGravity" to "isOverrideWorldGravity"),
        )

    }

}