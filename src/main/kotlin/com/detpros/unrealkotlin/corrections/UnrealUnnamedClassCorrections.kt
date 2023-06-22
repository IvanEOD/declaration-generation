package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.corrections.models.Corrections
import com.detpros.unrealkotlin.declaration.*
import com.detpros.unrealkotlin.utility.joinNames
import com.detpros.unrealkotlin.utility.toMemberLevel
import com.detpros.unrealkotlin.utility.toTopLevel


/**
 *  Unreal Unnamed Class Declarations Correction
 *
 * @author IvanEOD ( 6/22/2023 at 12:33 PM EST )
 */
class UnrealUnnamedClassCorrections(
    override val environment: CorrectionEnvironment,
    private val standardConfiguration: StandardDeclarationsCorrection,
    private val configuration: UnnamedClassDeclarationsCorrection,
) : Corrections() {

    private val ignorePropertyNames by lazy { standardConfiguration.ignorePropertyNames() }
    private val ignoreFunctionNames by lazy { standardConfiguration.ignoreFunctionNames() }
    private val renameMemberFunctions by lazy { standardConfiguration.definedFunctionRenames() }
    private val renameMemberProperties by lazy { standardConfiguration.definedPropertyRenames() }
    private val commonPrefixReplacements by lazy { standardConfiguration.commonPrefixReplacements() }

    private val definedMemberRenames by lazy { configuration.definedMemberRenames().toMutableMap() }
    private val unrealDefinedClassRenames by lazy { configuration.definedClassRenames() }

    private fun classConfig(declaration: ClassDeclaration) = configuration.classConfig(declaration)

    override fun correct(files: Set<FileDeclaration>) {
        with(environment) {
            _unnamedClasses
            genericsMap.forEach { (_, classes) ->
                var definedName: String? = null
                for (thing in classes) {
                    val checkName = unrealDefinedClassRenames[thing.name] ?: unrealDefinedClassRenames[thing.originalName]
                    if (checkName != null) {
                        definedName = checkName
                        break
                    }
                }
                val first = classes.first()
                if (definedName == null && first.properties.size == 1) {
                    val property = first.properties.first()
                    val propertyName = property.name
                    val propertyType = property.type
                    val typeName = propertyType.allNames().joinNames { !it.equals("kotlin", true) && !it.equals("js", true) }
                    val propertyNameLength = propertyName.length
                    val providerName =
                        (if (propertyNameLength == 1) typeName else propertyName.toTopLevel()) + "Provider"
                    if (isNotDefinedClass(providerName)) {
                        if (propertyNameLength == 1) {
                            val newPropertyName = typeName.toMemberLevel()
                            definedMemberRenames[providerName] = mutableMapOf(propertyName to newPropertyName)
                        }
                        definedName = providerName
                    }
                }
                if (definedName == null) definedName = first.name.replace("T$", "Object")

                classes.forEach klassForEach@ { klass ->
                    klass.rename("unnamedClassRename", definedName)
                    val klassConfig = classConfig(klass)
                    klassConfig?.correct(klass)
                    klass.lockRenaming()
                    if (klass != first) {
                        environment.addIgnoreClass(klass)
                        return@klassForEach
                    }
                    val memberCorrections = definedMemberRenames[definedName] ?: mapOf()

                    fun correctFunction(member: FunctionDeclaration) {
                        if (member.name in ignorePropertyNames) return
                        val rename = memberCorrections[member.name] ?: renameMemberFunctions[member.name]
                        if (rename != null) member.rename("standardClassFunctionCorrections",  rename)
                        else {
                            val newName = member.name.replaceAnyCommonPrefixes().toMemberLevel()
                            member.rename("standardClassFunctionElseCorrections", newName)
                        }
                        klassConfig?.functionConfig(member)?.correct(member)
                        member.lockRenaming()
                        member.members.filterIsInstance<ParameterDeclaration>().forEach {
                            val parameterName = it.name.toMemberLevel()
                            it.rename("standardClassFunctionParameterCorrections", parameterName)
                            it.lockRenaming()
                        }
                    }

                    fun correctProperty(member: PropertyDeclaration) {
                        if (member.name !in ignoreFunctionNames) {
                            val rename = memberCorrections[member.name] ?: renameMemberProperties[member.name]
                            if (rename != null) member.rename("standardClassPropertyCorrections", rename)
                            else {
                                val newName = member.name.replaceAnyCommonPrefixes().toMemberLevel()
                                member.rename("standardClassPropertyElseCorrections", newName)
                            }
                        }
                        klassConfig?.propertyConfig(member)?.correct(member)
                        member.lockRenaming()
                    }

                    fun correctClass(member: ClassDeclaration) {
                        environment.standardCorrections.internalCorrect(klass, setOf(member))
                    }

                    klass.members.forEach membersForEach@{ member ->
                        when (member) {
                            is ClassDeclarationImpl -> correctClass(member)
                            is FunctionDeclarationImpl -> correctFunction(member)
                            is PropertyDeclarationImpl -> correctProperty(member)
                            else -> {}
                        }
                    }
                    if (definedName.last().isDigit()) addUnnamedClass(klass)
                    else addFinishedClass(klass)
                }
            }
        }
    }

    private fun String.replaceAnyCommonPrefixes(): String =
        commonPrefixReplacements.entries.fold(this) { acc, (prefix, replacement) ->
            if (acc.startsWith(prefix)) acc.replaceFirst(prefix, replacement) else acc
        }

    private fun isNotDefinedClass(name: String): Boolean =
        name !in unrealDefinedClassRenames.values &&
        name !in unrealDefinedClassRenames.keys &&
        name !in definedMemberRenames.keys

}