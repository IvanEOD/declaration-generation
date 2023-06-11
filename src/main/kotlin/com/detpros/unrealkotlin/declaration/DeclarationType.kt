package com.detpros.unrealkotlin.declaration


/**
 *  Declaration Type
 *
 * @author IvanEOD ( 6/9/2023 at 12:14 PM EST )
 */
enum class DeclarationType {
    Class,
    Function,
    Property,
    Parameter,
    TypeAlias,
    Package,
    File,
    SuperInterface,
    TypeName,

    ;

    fun isType(declaration: Declaration) = when (this) {
        Class -> declaration is ClassDeclaration
        Function -> declaration is FunctionDeclaration
        Property -> declaration is PropertyDeclaration
        Parameter -> declaration is ParameterDeclaration
        TypeAlias -> declaration is TypeAliasDeclaration
        Package -> declaration is PackageDeclaration
        File -> declaration is FileDeclaration
        SuperInterface -> declaration is SuperInterfaceDeclaration
        TypeName -> declaration is TypeNameDeclaration
    }

}