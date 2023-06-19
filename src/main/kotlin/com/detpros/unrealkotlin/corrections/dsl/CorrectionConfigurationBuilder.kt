package com.detpros.unrealkotlin.corrections.dsl

import com.detpros.unrealkotlin.configuration.buildAll
import com.detpros.unrealkotlin.configuration.builders
import com.detpros.unrealkotlin.corrections.*


/**
 *  Correction Configuration Builder
 *
 * @author IvanEOD ( 6/19/2023 at 11:43 AM EST )
 */
class CorrectionConfigurationBuilder(
    private val enumsBuilder: EnumConfigurationBuilder = EnumConfigurationBuilder(),
    private val nonClassMembersBuilder: NonClassMemberConfigurationBuilder = NonClassMemberConfigurationBuilder(),
    private val standardBuilder: StandardConfigurationBuilder = StandardConfigurationBuilder(),
    private val unnamedClassesBuilder: UnnamedClassesConfigurationBuilder = UnnamedClassesConfigurationBuilder(),
) : ICorrectionConfigurationBuilder<CorrectionConfiguration, CorrectionConfigurationBuilder> {

    override fun include(other: CorrectionConfiguration) = apply {
        enumsBuilder.include(other.enumCorrections.toBuilder())
        nonClassMembersBuilder.include(other.nonClassMemberCorrections.toBuilder())
        standardBuilder.include(other.standardCorrections.toBuilder())
        unnamedClassesBuilder.include(other.unnamedClasses.toBuilder())
    }

    override fun include(other: CorrectionConfigurationBuilder) = apply {
        enumsBuilder.include(other.enumsBuilder)
        nonClassMembersBuilder.include(other.nonClassMembersBuilder)
        standardBuilder.include(other.standardBuilder)
        unnamedClassesBuilder.include(other.unnamedClassesBuilder)
    }

    fun enums(block: EnumConfigurationBuilder.() -> Unit) = apply {
        enumsBuilder.block()
    }

    fun nonClassMembers(block: NonClassMemberConfigurationBuilder.() -> Unit) = apply {
        nonClassMembersBuilder.block()
    }

    fun standard(block: StandardConfigurationBuilder.() -> Unit) = apply {
        standardBuilder.block()
    }

    fun unnamedClasses(block: UnnamedClassesConfigurationBuilder.() -> Unit) = apply {
        unnamedClassesBuilder.block()
    }

    override fun build(): CorrectionConfiguration = CorrectionConfiguration(
        enumsBuilder.build(),
        nonClassMembersBuilder.build(),
        standardBuilder.build(),
        unnamedClassesBuilder.build()
    )
}

class EnumConfigurationBuilder(
    classes: List<ClassConfigurationBuilder> = emptyList()
) : ICorrectionConfigurationBuilder<EnumCorrectionsConfiguration, EnumConfigurationBuilder> {
    private val classes = classes.toMutableList()

    fun named(name: String, block: ClassConfigurationBuilder.() -> Unit) = apply {
        classes.add(ClassConfigurationBuilder(name).apply(block))
    }
    fun rename(name: String, newName: String) = named(name) { rename(newName) }

    override fun include(other: EnumCorrectionsConfiguration) = apply {
        classes.addAll(other.classes.builders())
    }
    override fun include(other: EnumConfigurationBuilder) = apply {
        classes.addAll(other.classes)
    }

    override fun build(): EnumCorrectionsConfiguration = EnumCorrectionsConfiguration(classes.buildAll())
}

class NonClassMemberConfigurationBuilder(
    typeAliasRenames: Map<String, String> = emptyMap(),
    propertyRenames: Map<String, String> = emptyMap(),
) : ICorrectionConfigurationBuilder<NonClassMemberCorrectionsConfiguration, NonClassMemberConfigurationBuilder> {
    private val typeAliasRenames = typeAliasRenames.toMutableMap()
    private val propertyRenames = propertyRenames.toMutableMap()

    fun renameTypeAlias(typeAlias: String, newName: String) = apply {
        typeAliasRenames[typeAlias] = newName
    }

    fun renameProperty(property: String, newName: String) = apply {
        propertyRenames[property] = newName
    }

    override fun include(other: NonClassMemberCorrectionsConfiguration) = apply {
        typeAliasRenames.putAll(other.typeAliasRenames)
        propertyRenames.putAll(other.propertyRenames)
    }
    override fun include(other: NonClassMemberConfigurationBuilder) = apply {
        typeAliasRenames.putAll(other.typeAliasRenames)
        propertyRenames.putAll(other.propertyRenames)
    }

    override fun build(): NonClassMemberCorrectionsConfiguration = NonClassMemberCorrectionsConfiguration(
        typeAliasRenames,
        propertyRenames
    )

}

class StandardConfigurationBuilder(
    commonPrefixReplacements: Map<String, String> = emptyMap(),
    ignoreFunctions: List<String> = emptyList(),
    ignoreProperties: List<String> = emptyList(),
    allMemberFunctions: List<FunctionConfigurationBuilder> = emptyList(),
    allMemberProperties: List<PropertyConfigurationBuilder> = emptyList(),
    allMembers: List<MemberConfigurationBuilderImpl> = emptyList(),
    classes: List<ClassConfigurationBuilder> = emptyList()
) : ConfigurationBuilderWithMembers<StandardCorrectionsConfiguration, StandardConfigurationBuilder>,
    ConfigurationBuilderWithClasses<StandardCorrectionsConfiguration, StandardConfigurationBuilder> {

    private val commonPrefixReplacements = commonPrefixReplacements.toMutableMap()
    private val ignoreFunctions = ignoreFunctions.toMutableSet()
    private val ignoreProperties = ignoreProperties.toMutableSet()
    private val allMemberFunctions = allMemberFunctions.toMutableList()
    private val allMemberProperties = allMemberProperties.toMutableList()
    private val allMembers = allMembers.toMutableList()
    private val classes = classes.toMutableList()

    override fun memberConfiguration(configuration: MemberConfigurationBuilderImpl) = apply {
        allMembers.add(configuration)
    }

    override fun functionConfiguration(configuration: FunctionConfigurationBuilder) = apply {
        allMemberFunctions.add(configuration)
    }

    override fun propertyConfiguration(configuration: PropertyConfigurationBuilder) = apply {
        allMemberProperties.add(configuration)
    }

    override fun classConfiguration(configuration: ClassConfigurationBuilder) = apply {
        classes.add(configuration)
    }

    fun replaceCommonPrefix(prefix: String, replacement: String) = apply {
        commonPrefixReplacements[prefix] = replacement
    }

    fun ignoreFunction(name: String) = apply {
        ignoreFunctions.add(name)
    }

    fun ignoreFunctions(vararg name: String) = apply {
        ignoreFunctions.addAll(name)
    }

    fun ignoreProperty(name: String) = apply {
        ignoreProperties.add(name)
    }

    fun ignoreProperties(vararg name: String) = apply {
        ignoreProperties.addAll(name)
    }

    override fun include(other: StandardCorrectionsConfiguration) = apply {
        commonPrefixReplacements.putAll(other.commonPrefixReplacements)
        ignoreFunctions.addAll(other.ignoreFunctions)
        ignoreProperties.addAll(other.ignoreProperties)
        allMemberFunctions.addAll(other.allMemberFunctions.builders())
        allMemberProperties.addAll(other.allMemberProperties.builders())
        allMembers.addAll(other.allMembers.map { it.toBuilder() as MemberConfigurationBuilderImpl })
        classes.addAll(other.classes.builders())
    }

    override fun include(other: StandardConfigurationBuilder) = apply {
        commonPrefixReplacements.putAll(other.commonPrefixReplacements)
        ignoreFunctions.addAll(other.ignoreFunctions)
        ignoreProperties.addAll(other.ignoreProperties)
        allMemberFunctions.addAll(other.allMemberFunctions)
        allMemberProperties.addAll(other.allMemberProperties)
        allMembers.addAll(other.allMembers)
        classes.addAll(other.classes)
    }

    override fun build(): StandardCorrectionsConfiguration = StandardCorrectionsConfiguration(
        commonPrefixReplacements,
        ignoreFunctions.toList(),
        ignoreProperties.toList(),
        allMemberFunctions.buildAll(),
        allMemberProperties.buildAll(),
        allMembers.buildAll(),
        classes.buildAll()
    )

}

class UnnamedClassesConfigurationBuilder(
    classes: List<ClassConfigurationBuilder> = emptyList()
) : ConfigurationBuilderWithClasses<UnnamedClassesConfiguration, UnnamedClassesConfigurationBuilder> {
    private val classes = classes.toMutableList()
    override fun classConfiguration(configuration: ClassConfigurationBuilder) = apply {
        classes.add(configuration)
    }

    override fun include(other: UnnamedClassesConfiguration) = apply {
        classes.addAll(other.classes.builders())
    }

    override fun include(other: UnnamedClassesConfigurationBuilder) = apply {
        classes.addAll(other.classes)
    }

    override fun build(): UnnamedClassesConfiguration = UnnamedClassesConfiguration(classes.buildAll())
}

sealed interface ConfigurationBuilderWithMembers<T : ICorrectionConfiguration<T, B>, B : ConfigurationBuilderWithMembers<T, B>>: ICorrectionConfigurationBuilder<T, B> {

    fun memberConfiguration(configuration: MemberConfigurationBuilderImpl): B
    fun functionConfiguration(configuration: FunctionConfigurationBuilder): B
    fun propertyConfiguration(configuration: PropertyConfigurationBuilder): B

    fun renameMember(name: String, newName: String) =
        memberConfiguration(MemberConfigurationBuilderImpl(name, newName))

    fun property(
        name: String,
        type: String? = null,
        block: PropertyConfigurationBuilder.() -> Unit
    ) = propertyConfiguration(PropertyConfigurationBuilder(name, type = type).apply(block))

    fun function(
        name: String,
        returnType: String? = null,
        block: FunctionConfigurationBuilder.() -> Unit
    ) = functionConfiguration(FunctionConfigurationBuilder(name, returnType = returnType).apply(block))

    fun renameProperty(
        name: String,
        newName: String,
        type: String? = null
    ) = property(name, type) { rename(newName) }

    fun renameFunction(
        name: String,
        newName: String,
        returnType: String? = null
    ) = function(name, returnType) { rename(newName) }

}

sealed interface ConfigurationBuilderWithClasses<T: ICorrectionConfiguration<T, B>, B : ConfigurationBuilderWithClasses<T, B>>: ICorrectionConfigurationBuilder<T, B> {

    fun classConfiguration(configuration: ClassConfigurationBuilder): B

    fun klass(name: String, block: ClassConfigurationBuilder.() -> Unit) =
        classConfiguration(ClassConfigurationBuilder(name).apply(block))

    fun klassWithSupertype(type: String, block: ClassConfigurationBuilder.() -> Unit) =
        classConfiguration(ClassConfigurationBuilder("", superType = type).apply(block))

    fun renameClass(name: String, newName: String) = klass(name) { rename(newName) }

}

