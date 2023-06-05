package com.detpros.unrealkotlin.declaration

import com.squareup.kotlinpoet.*


/**
 *  Extensions
 *
 * @author IvanEOD ( 5/24/2023 at 11:01 AM EST )
 */

fun AnnotationSpec.toDeclaration(): AnnotationDeclaration = AnnotationDeclaration.fromAnnotationSpec(this)
fun TypeSpec.toDeclaration(): ClassDeclaration = ClassDeclaration.fromTypeSpec(this)
fun ParameterSpec.toDeclaration(): ParameterDeclaration = ParameterDeclaration.fromParameterSpec(this)
fun CodeBlock.toDeclaration(): CodeDeclaration = CodeDeclaration.fromCodeBlock(this)
fun PropertySpec.toDeclaration(): PropertyDeclaration = PropertyDeclaration.fromPropertySpec(this)
fun TypeAliasSpec.toDeclaration(): TypeAliasDeclaration = TypeAliasDeclaration.fromTypeAliasSpec(this)
fun FileSpec.toDeclaration(): FileDeclaration = FileDeclaration.fromFileSpec(this)
fun FunSpec.toDeclaration(): FunctionDeclaration = FunctionDeclaration.fromFunSpec(this)

fun Map.Entry<TypeName, CodeBlock?>.toDeclaration(): SuperInterfaceDeclaration = SuperInterfaceDeclaration.fromSuperInterface(this)
fun Map<TypeName, CodeBlock?>.toDeclarations(): Set<SuperInterfaceDeclaration> = map(Map.Entry<TypeName, CodeBlock?>::toDeclaration).toSet()
fun Pair<TypeName, CodeBlock?>.toDeclaration(): SuperInterfaceDeclaration = SuperInterfaceDeclaration.fromSuperInterface(this.first, this.second)


fun TypeName.toDeclaration(): TypeNameDeclaration = TypeNameDeclaration.fromTypeName(this)
fun ClassName.toDeclaration(): ClassNameDeclaration = ClassNameDeclaration.fromClassName(this)
fun LambdaTypeName.toDeclaration(): LambdaTypeNameDeclaration = LambdaTypeNameDeclaration.fromLambdaTypeName(this)
fun TypeVariableName.toDeclaration(): TypeVariableNameDeclaration = TypeVariableNameDeclaration.fromTypeVariableName(this)
fun ParameterizedTypeName.toDeclaration(): ParameterizedTypeNameDeclaration = ParameterizedTypeNameDeclaration.fromParameterizedTypeName(this)
fun WildcardTypeName.toDeclaration(): WildcardTypeNameDeclaration = WildcardTypeNameDeclaration.fromWildcardTypeName(this)
fun Dynamic.toDeclaration(): DynamicTypeNameDeclaration = DynamicTypeNameDeclaration

val FileSpec.classes: List<TypeSpec> get() = members.filterIsInstance(TypeSpec::class.java)
val FileSpec.functions: List<FunSpec> get() = members.filterIsInstance(FunSpec::class.java)
val FileSpec.properties: List<PropertySpec> get() = members.filterIsInstance(PropertySpec::class.java)
val FileSpec.typeAliases: List<TypeAliasSpec> get() = members.filterIsInstance(TypeAliasSpec::class.java)
val FileSpec.imports: List<Import> get() = toBuilder(packageName, name).imports

fun FileSpec.Builder.addDefaultAnnotations() = addAnnotation(
    AnnotationSpec.builder(Suppress::class)
        .addMember("%S", "INTERFACE_WITH_SUPERCLASS")
        .addMember("%S", "OVERRIDING_FINAL_MEMBER")
        .addMember("%S", "RETURN_TYPE_MISMATCH_ON_OVERRIDE")
        .addMember("%S", "CONFLICTING_OVERLOADS")
        .build()
)