package com.detpros.unrealkotlin.declaration

import com.detpros.unrealkotlin.utility.GenerationEnvironment
import com.squareup.kotlinpoet.*

/**
 *  Type Name Declaration
 *
 * @author IvanEOD ( 5/23/2023 at 2:45 PM EST )
 */
sealed interface TypeNameDeclaration : Declaration {


    fun isName(name: String): Boolean
    fun isSameAs(typeName: TypeName): Boolean
    fun isSameAs(other: TypeNameDeclaration): Boolean

    fun toTypeName(): TypeName

    override val members: Set<Declaration>

    fun allNames(): List<String>


    companion object {
        fun fromTypeName(typeName: TypeName): TypeNameDeclaration = when (typeName) {
            is ClassName -> typeName.toDeclaration()
            is Dynamic -> DynamicTypeNameDeclaration
            is LambdaTypeName -> typeName.toDeclaration()
            is ParameterizedTypeName -> typeName.toDeclaration()
            is TypeVariableName -> typeName.toDeclaration()
            is WildcardTypeName -> typeName.toDeclaration()
        }

        val Any = ClassNameDeclaration.fromClassName(ANY)
        val Array = ClassNameDeclaration.fromClassName(ARRAY)
        val Unit = ClassNameDeclaration.fromClassName(UNIT)
        val Boolean = ClassNameDeclaration.fromClassName(BOOLEAN)
        val Byte = ClassNameDeclaration.fromClassName(BYTE)
        val Short = ClassNameDeclaration.fromClassName(SHORT)
        val Int = ClassNameDeclaration.fromClassName(INT)
        val Long = ClassNameDeclaration.fromClassName(LONG)
        val Char = ClassNameDeclaration.fromClassName(CHAR)
        val Float = ClassNameDeclaration.fromClassName(FLOAT)
        val Double = ClassNameDeclaration.fromClassName(DOUBLE)
        val String = ClassNameDeclaration.fromClassName(STRING)
        val CharSequence = ClassNameDeclaration.fromClassName(CHAR_SEQUENCE)
        val Comparable = ClassNameDeclaration.fromClassName(COMPARABLE)
        val Throwable = ClassNameDeclaration.fromClassName(THROWABLE)
        val Annotation = ClassNameDeclaration.fromClassName(ANNOTATION)
        val Nothing = ClassNameDeclaration.fromClassName(NOTHING)
        val Number = ClassNameDeclaration.fromClassName(NUMBER)
        val Iterable = ClassNameDeclaration.fromClassName(ITERABLE)
        val Collection = ClassNameDeclaration.fromClassName(COLLECTION)
        val List = ClassNameDeclaration.fromClassName(LIST)
        val Set = ClassNameDeclaration.fromClassName(SET)
        val Map = ClassNameDeclaration.fromClassName(MAP)
        val MapEntry = ClassNameDeclaration.fromClassName(MAP_ENTRY)
        val MutableIterable = ClassNameDeclaration.fromClassName(MUTABLE_ITERABLE)
        val MutableCollection = ClassNameDeclaration.fromClassName(MUTABLE_COLLECTION)
        val MutableList = ClassNameDeclaration.fromClassName(MUTABLE_LIST)
        val MutableSet = ClassNameDeclaration.fromClassName(MUTABLE_SET)
        val MutableMap = ClassNameDeclaration.fromClassName(MUTABLE_MAP)
        val MutableMapEntry = ClassNameDeclaration.fromClassName(MUTABLE_MAP_ENTRY)
        val BooleanArray = ClassNameDeclaration.fromClassName(BOOLEAN_ARRAY)
        val ByteArray = ClassNameDeclaration.fromClassName(BYTE_ARRAY)
        val CharArray = ClassNameDeclaration.fromClassName(CHAR_ARRAY)
        val ShortArray = ClassNameDeclaration.fromClassName(SHORT_ARRAY)
        val IntArray = ClassNameDeclaration.fromClassName(INT_ARRAY)
        val LongArray = ClassNameDeclaration.fromClassName(LONG_ARRAY)
        val FloatArray = ClassNameDeclaration.fromClassName(FLOAT_ARRAY)
        val DoubleArray = ClassNameDeclaration.fromClassName(DOUBLE_ARRAY)
        val Enum = ClassNameDeclaration.fromClassName(ENUM)
        val UByte = ClassNameDeclaration.fromClassName(U_BYTE)
        val UShort = ClassNameDeclaration.fromClassName(U_SHORT)
        val UInt = ClassNameDeclaration.fromClassName(U_INT)
        val ULong = ClassNameDeclaration.fromClassName(U_LONG)
        val UByteArray = ClassNameDeclaration.fromClassName(U_BYTE_ARRAY)
        val UShortArray = ClassNameDeclaration.fromClassName(U_SHORT_ARRAY)
        val UIntArray = ClassNameDeclaration.fromClassName(U_INT_ARRAY)
        val ULongArray = ClassNameDeclaration.fromClassName(U_LONG_ARRAY)



        private val defaultTypes = listOf(
                Any,
                Array,
                Unit,
                Boolean,
                Byte,
                Short,
                Int,
                Long,
                Char,
                Float,
                Double,
                String,
                CharSequence,
                Comparable,
                Throwable,
                Annotation,
                Nothing,
                Number,
                Iterable,
                Collection,
                List,
                Set,
                Map,
                MapEntry,
                MutableIterable,
                MutableCollection,
                MutableList,
                MutableSet,
                MutableMap,
                MutableMapEntry,
                BooleanArray,
                ByteArray,
                CharArray,
                ShortArray,
                IntArray,
                LongArray,
                FloatArray,
                DoubleArray,
                Enum,
                UByte,
                UShort,
                UInt,
                ULong,
                UByteArray,
                UShortArray,
                UIntArray,
                ULongArray,
            )

        init {
            defaultTypes.forEach { GenerationEnvironment.addAutoImport(it.toTypeName()) }
        }

    }

}




