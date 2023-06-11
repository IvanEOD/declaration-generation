package com.detpros.unrealkotlin.corrections


import com.detpros.unrealkotlin.declaration.*
import com.detpros.unrealkotlin.utility.joinNames
import com.detpros.unrealkotlin.utility.toMemberLevel
import com.detpros.unrealkotlin.utility.toTopLevel

/**
 *  Unnamed Class Corrections
 *
 * @author IvanEOD ( 5/26/2023 at 3:15 PM EST )
 */
class UnnamedClassCorrections(
    override val environment: CorrectionEnvironment,
    private val standardConfiguration: StandardCorrectionsConfiguration,
    private val configuration: UnnamedClassesConfiguration,
) : Corrections() {

    private val ignorePropertyNames by lazy { standardConfiguration.ignorePropertyNames() }
    private val ignoreFunctionNames by lazy { standardConfiguration.ignoreFunctionNames() }
    private val renameMemberFunctions by lazy { standardConfiguration.definedFunctionRenames() }
    private val renameMemberProperties by lazy { standardConfiguration.definedPropertyRenames() }
    private val commonPrefixReplacements by lazy { standardConfiguration.commonPrefixReplacements() }

    private val definedMemberRenames by lazy { configuration.definedMemberRenames().toMutableMap() }
    private val unrealDefinedClassRenames by lazy { configuration.definedClassRenames() }


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

    companion object {




//        private val unrealDefinedClassRenames = mapOf(
//            "T$0" to "GuidProvider",
//            "T$1" to "InitialSeedProvider",
//            "T$2" to "RandomStreamProvider",
//            "T$3" to "RotationProvider",
//            "T$4" to "AxesProvider",
//            "T$5" to "OrientationAndPositionProvider",
//            "T$6" to "SpringStateProvider",
//            "T$9" to "QuatProvider",
//            "T$11" to "Vector4ComponentsProvider",
//            "T$12" to "Vector4Provider",
//            "T$14" to "MatrixProvider",
//            "T$49" to "StringResultProvider",
//            "T$83" to "AnimPoseProvider",
//            "T$91" to "TransformProvider",
//            "T$125" to "AnimBoneCompressionSettingsProvider",
//            "T$126" to "AnimBoneCompressionCurveProvider",
//            "T$143" to "MetaDataProvider",
//            "T$144" to "MetaDataOfClassProvider",
//            "T$150" to "OutAnimPoseProvider",
//            "T$180" to "SkeletalMeshOutBuildOptionsProvider",
//            "T$240" to "MeshOutBuildOptionsProvider",
//            "T$291" to "AnyParameterNamesProvider",
//            "T$414" to "UnrealEngineClassesResultProvider",
//            "T$497" to "StringArrayProvider",
//            "T$525" to "JavascriptConnectionParamsProvider",
//            "T$722" to "NumberValueProvider",
//            "T$726" to "OutPathValueProvider",
//            "T$728" to "AnyResultProvider",
//            "T$729" to "NumberArrayResultProvider",
//            "T$743" to "InputAxisKeyMappingOutProvider",
//            "T$745" to "InputActionKeyMappingOutProvider",
//            "T$832" to "ControlRigControlOutPoseProvider",
//        )
//        private val numberValueProvider = mapOf(
//            "value" to "numberValue",
//            "Value" to "numberValue",
//            "$" to "value"
//        )
//        private val definedMemberRenames = mutableMapOf(
//            "MatrixProvider" to mutableMapOf("M" to "matrix"),
//            "Vector4Provider" to mutableMapOf("A" to "vector4"),
//            "QuatProvider" to mutableMapOf("Q" to "quat"),
//            "NumberValueProvider" to numberValueProvider
//        )
    }


}