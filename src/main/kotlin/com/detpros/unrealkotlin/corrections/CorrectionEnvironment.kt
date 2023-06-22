package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.declaration.*
import com.detpros.unrealkotlin.utility.*
import com.detpros.unrealkotlin.utility.GenericClass
import com.squareup.kotlinpoet.KModifier
import java.io.File

/**
 *  Correction Environment
 *
 * @author IvanEOD ( 5/26/2023 at 3:13 PM EST )
 */

class CorrectionEnvironment(
    private val sourceDestination: File,
    private val declarations: PackageDeclaration,
    private val configuration: UnrealDeclarationsCorrection,
    private val requiredClasses: Set<String> = setOf(),
    private val includeAllClasses: Boolean = false,
    private val includeAllEnums: Boolean = true,
) {

    val enumCorrections = UnrealEnumCorrections(this, configuration.enumCorrections)
    val nonClassMemberFileCorrections = UnrealNonClassMemberCorrections(this, configuration.nonClassMemberCorrections)
    val unnamedClassCorrections = UnrealUnnamedClassCorrections(this, configuration.standardCorrections, configuration.unnamedClasses)
    val standardCorrections = UnrealStandardCorrections(this, configuration.standardCorrections)

    internal val genericsMap = mutableMapOf<GenericClass, MutableSet<ClassDeclaration>>()

    private val files: Set<FileDeclaration> get() = declarations.files
    private val ignoreClasses = mutableSetOf<ClassDeclaration>()
    private val enums = mutableSetOf<ClassDeclaration>()
    private val fileNonClasses = mutableSetOf<DeclarationWithName>()
    private val finishedClasses = mutableSetOf<ClassDeclaration>()
    private val unnamedClasses = mutableSetOf<ClassDeclaration>()
    private val completedClasses = mutableSetOf<Declaration>()

    fun isIgnore(declaration: Declaration): Boolean =
        declaration in completedClasses || (declaration is ClassDeclaration && declaration in ignoreClasses)

    fun allTopLevelClasses() = files.asSequence()
        .onEach(Declaration::refresh)
        .flatMap(Declaration::members)
        .filterIsInstance<ClassDeclaration>()

    fun allClassNames() = files.asSequence()
        .onEach(Declaration::refresh)
        .flatMap(Declaration::allMembers)
        .filterIsInstance<ClassDeclaration>()
        .map(ClassDeclaration::name)

    private fun addToCompleteClasses(declaration: Declaration): Boolean {
        if (declaration !is ClassDeclaration) return completedClasses.add(declaration)
        if (completedClasses.filterIsInstance<ClassDeclaration>().any { it.name == declaration.name }) return false
        return completedClasses.add(declaration)
    }

    fun addIgnoreClass(declaration: ClassDeclaration) {
        ignoreClasses.add(declaration)
    }

    fun addEnumClass(declaration: ClassDeclaration) {
        if (addToCompleteClasses(declaration)) enums.add(declaration)
    }

    fun addFileNonClass(declaration: DeclarationWithName) {
        if (addToCompleteClasses(declaration)) fileNonClasses.add(declaration)
    }

    fun addFinishedClass(declaration: ClassDeclaration) {
        if (addToCompleteClasses(declaration)) finishedClasses.add(declaration)
    }

    fun addUnnamedClass(declaration: ClassDeclaration) {
        if (addToCompleteClasses(declaration)) unnamedClasses.add(declaration)
    }

    internal val _unnamedClasses by lazy {
        files.asSequence()
            .onEach(Declaration::refresh)
            .flatMap(Declaration::members)
            .filterIsInstance<ClassDeclaration>()
            .filter { it.name.startsWith("T$") }
            .onEach {
                val generic =
                    GenericClass(it.properties.map { property -> property.name to property.type.toString() }.toSet())
                val mapped = genericsMap.getOrPut(generic) { mutableSetOf() }
                mapped.add(it)
            }
            .toSet()
    }


    fun process() {

        enumCorrections.correct(declarations.files)
        nonClassMemberFileCorrections.correct(declarations.files)
        unnamedClassCorrections.correct(declarations.files)
        standardCorrections.correct(declarations.files)

        files.asSequence()
            .onEach(Declaration::refresh)
            .forEach { file ->
                val members = file.members
                ignoreClasses.forEach { if (it in members) file.removeClass(it) }
            }

        files.asSequence()
            .onEach(Declaration::refresh)
            .flatMap(Declaration::allMembers)
            .distinct()
            .onEach {
                when (it) {
                    is ClassDeclaration -> {
                        if (it.hasSuperType("InputEvent")) {
                            val cloneFunction = it.functions.find { function -> function.name == "clone" } ?: return@onEach
                            if (!cloneFunction.isOverride) cloneFunction.addModifier(KModifier.OVERRIDE)
                        }
                    }
                    is FunctionDeclaration -> {
                        if ((it.isOverride || it.name == it.jsName) && it.isJsNamePresent) it.removeJsName()
                    }
                    is PropertyDeclaration -> {
                        if ((it.isOverride || it.name == it.jsName) && it.isJsNamePresent) it.removeJsName()
                    }
                    is TypeAliasDeclaration -> {}
                    is ParameterDeclaration -> {
                        if (it.name == "fn") it.rename("mainParameterDeclaration", "function")
                        val lowered = it.name.toMemberLevel()
                        if (lowered != it.name) {
                            if (it.isRenamingLocked) it.unlockRenaming()
                            it.rename("mainParameterDeclarationLowering", lowered)
                            it.lockRenaming()
                        }
                    }
                    else -> {}
                }
            }

        ClassNameDeclarationImpl.forEach {
            if (it.packageName.isEmpty() || it.packageName == "tsstdlib") it.setPackage("ue")
        }
        files.asSequence()
            .onEach(Declaration::refresh)
            .flatMap(Declaration::members)
            .filterIsInstance<ClassDeclaration>()
            .filter { it.properties.size > 1 }
            .filter { it.properties.size != it.properties.distinctBy(PropertyDeclaration::name).size }
            .onEach { klass ->
                println("Checking ${klass.name}, ${klass.properties.size} properties")
                val matchingNames = klass.properties
                    .groupBy(PropertyDeclaration::name)
                    .filter { it.value.size > 1 }
                    .map { it.key }
                    .toSet()
                println("Matching names: ${matchingNames.joinToString { "$it, " }}")
                for (name in matchingNames) {
                    val booleanProperty = klass.properties.find { it.name == name && it.type.isName("Boolean") } ?: continue
                    if (booleanProperty.isRenamingLocked) booleanProperty.unlockRenaming()
                    booleanProperty.rename("", "is" + name.capitalizeFirst())
                    booleanProperty.lockRenaming()
                }
            }

        val sharedNames = mutableMapOf<String, MutableSet<ClassDeclaration>>()
        files.asSequence()
            .onEach(Declaration::refresh)
            .flatMap(Declaration::members)
            .filterIsInstance<ClassDeclaration>()
            .forEach { klass ->
                val name = klass.name
                val set = sharedNames.getOrPut(name) { mutableSetOf() }
                set.add(klass)
            }
        sharedNames.filter { it.value.size > 1 }.forEach { (name, classes) ->
            classes.forEach { klass ->
                klass.properties.firstOrNull()?.let { first ->
                    val newName = (listOf(name.trim().replace("Provider", "")) +  first.type.allNames())
                        .joinNames { !it.equals("kotlin", true) && !it.equals("js", true) } + "Provider"
                    klass.unlockRenaming()
                    klass.rename("sharedName", newName)
                    klass.lockRenaming()
                }
            }
        }


        files.asSequence()
            .flatMap { it.classes.filter { klass -> klass.name in mediaSources } }
            .forEach { klass ->
                val removeFunctions = klass.functions.filter { function -> function.hasTypeVariables() }
                removeFunctions.forEach { function -> klass.removeFunction(function) }
                klass.functions.forEach { function -> function.removeModifier(KModifier.OVERRIDE) }
            }

        val deleteClassNames = configuration.classConfigurations().filter { it.delete }.map { it.name }
        val deleteClasses = files.flatMap { it.classes }.filter { it.originalName in deleteClassNames }.toList()
        files.forEach { file -> deleteClasses.forEach { file.removeClass(it) } }

//        checkForErrors()
//        writeFiles(sourceDestination)

        if (!includeAllClasses) {
            val classDependencies = files.flatMap(Declaration::allMembers)
                .filterIsInstance<ClassDeclaration>().map {
                    ClassDependencies(it.name,
                        it.allMembers.filterIsInstance<TypeNameDeclaration>().map { type -> type.allNames().last() }
                            .toSet()
                    )
                }.toSet()

            val managedDependencies = ManagedDependencies(classDependencies)

            val classesToInclude = (requiredClasses + minClasses).toMutableSet()
            if (includeAllEnums) classesToInclude += UnrealEnumCorrections.unrealEnumTypeNames
            val requiredClassNames = managedDependencies.getAllDependencies(classesToInclude)

            files.asSequence()
                .forEach { file ->
                    val forRemove = file.classes.filter { it.name !in requiredClassNames }
                    forRemove.forEach { file.removeClass(it) }
                    file.refresh()
                }

        }

        writeFiles(sourceDestination)

    }

    private fun writeFiles(destination: File) {
        files.asSequence()
            .onEach(Declaration::refresh)
            .forEach {
                val stringBuilder = StringBuilder()
                it.toFileSpec().writeTo(stringBuilder)
                val file = File(destination, (fileNames[it.name] ?: it.name) + ".kt" )
                FileFinalization(file, stringBuilder.toString())
            }
        val ue5 = File(destination, "UE5.kt")
        ue5.writeText(kotlinUE5Text())
    }

    private val mediaSources = setOf(
        "PlatformMediaSource",
        "BaseMediaSource"
    )



    private fun String.capitalizeFirst() = substring(0, 1).uppercase() + substring(1)

    val doNotRemoveUnderscores = mutableSetOf<String>()

    fun safeRemoveUnderscores(value: String): String =
        if (value in doNotRemoveUnderscores) value
        else value.replace("_", "")




    private fun checkForErrors() {

        checkForPropertyNameConflicts()

    }


    private fun checkForPropertyNameConflicts() {
        val duplicateProperties = mutableMapOf<String, MutableSet<String>>()

        files.asSequence()
            .flatMap(Declaration::members)
            .filterIsInstance<ClassDeclaration>()
            .forEach { klass ->
                val names = mutableSetOf<String>()
                val (added, duplicates) = klass.properties.partition { names.add(it.name) }
                if (duplicates.isNotEmpty()) duplicateProperties.getOrPut(klass.name) { mutableSetOf() } += duplicates.map(PropertyDeclaration::name)
            }

        println("Property name conflicts:")
        duplicateProperties.forEach { (className, properties) ->
            println("Class $className: ${properties.joinToString(", ")}")
        }

    }

    companion object {
        private val fileNames = mapOf(
            "ue" to "UE",
            "_part_0_ue" to "UE0",
            "_part_1_ue" to "UE1",
            "_part_2_ue" to "UE2",
            "_part_3_ue" to "UE3",
            "_part_4_ue" to "UE4",
        )

        private val minClasses = setOf("UObject", "KotlinObject")




    }

}