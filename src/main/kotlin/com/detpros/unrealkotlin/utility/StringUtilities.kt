package com.detpros.unrealkotlin.utility

import net.pearx.kasechange.toCamelCase
import net.pearx.kasechange.toPascalCase


fun commonPrefix(names: Set<String>): String? {
    if (names.isEmpty()) return null
    if (names.size == 1) return names.first()
    return names.zipWithNext().map { (first, second) -> first.commonPrefixWith(second) }.minByOrNull { common -> common.length }
}
fun Iterable<String>.commonPrefix() = commonPrefix(this.toSet())

private val String.isAllCaps: Boolean get() = this.all { it.isUpperCase() }
private fun String.isBooleanNeedingRenamed() = this.startsWith("b") && this.length > 1 && this[1].isUpperCase()

private val acronymBeforeUnderscoreRegex = Regex("^([A-Z]*)_")
private val acronymAfterUnderscoreRegex = Regex("_([A-Z]*)$")

fun standardFixName(isPascal: Boolean = false, input: String, step: Int = 0): String {
    if (step == 0 && input.length == 1) return input.lowercase()
    if (input.isAllCaps) return input
    val name = input.replace("_", "")
    if (name.isBooleanNeedingRenamed()) return standardFixName(isPascal, name.substring(1), step + 1)
    if (acronymBeforeUnderscoreRegex.matches(name)) {
        val acronym = name.substringBefore("_")
        val remainder = name.substringAfter("_")
        return (acronym + standardFixName(isPascal, remainder, step + 1))
    }
    if (acronymAfterUnderscoreRegex.matches(name)) {
        val acronym = name.substringAfter("_")
        val before = name.substringBefore("_")
        return (standardFixName(isPascal, before, step + 1) + acronym)
    }

    fun fixCaps(name: String): String = if (isPascal) name.toPascalCase() else name.toCamelCase()

    var returnName = ""
    var lastChunk = ""
    if (name.contains('_')) {
        var addChunk = ""
        name.split('_').forEach { chunk ->
            if (chunk.isEmpty()) return@forEach
            addChunk = if (chunk.isAllCaps) chunk else fixCaps(chunk)
            if (lastChunk.isAllCaps) addChunk = "_${addChunk}_"
            returnName += addChunk
            lastChunk = chunk
        }
    } else returnName = fixCaps(name)
    return returnName.replace("__", "_").removePrefix("_").removeSuffix("_")
}

fun String.toTopLevel() = standardFixName(true, this)
fun String.toMemberLevel() = standardFixName(false, this)
fun String.isTextReplaceableNumber(): Boolean = this.toIntOrNull() != null && this in numberToNamesMap.keys
fun String.numberToText(): String = numberToNamesMap[this] ?: this
fun String.toNumbersString(): String {
    if (this.isTextReplaceableNumber()) return this.numberToText()
    val numbers = this.takeWhile { it.isDigit() }
    if (numbers.isTextReplaceableNumber()) return numbers.numberToText() + this.substring(numbers.length)
    return when (numbers.length) {
        0 -> this
        1 -> numberToText()
        2 -> numberToText()
        3 -> {
            val firstHalf = numbers.substring(0, 1)
            val secondHalf = numbers.substring(1, 3)
            firstHalf.numberToText() + secondHalf.numberToText()
        }
        4 -> {
            val firstHalf = numbers.substring(0, 2)
            val secondHalf = numbers.substring(2, 4)
            firstHalf.numberToText() + secondHalf.numberToText()
        }
        else -> this.chunked(2).joinToString("") { it.numberToText() }
    }
}

fun String.removeNonAlphaNumeric(): String = this.replace(Regex("[^A-Za-z0-9]"), "")
fun Collection<String>.joinNames(filter: (String) -> Boolean): String = this.map(String::trim)
    .filter(filter)
    .joinToString("") { it.removeNonAlphaNumeric() }

private val numberToNamesMap = mapOf(
    "0" to "Zero",
    "1" to "One",
    "2" to "Two",
    "3" to "Three",
    "4" to "Four",
    "5" to "Five",
    "6" to "Six",
    "7" to "Seven",
    "8" to "Eight",
    "9" to "Nine",
    "10" to "Ten",
    "11" to "Eleven",
    "12" to "Twelve",
    "13" to "Thirteen",
    "14" to "Fourteen",
    "15" to "Fifteen",
    "16" to "Sixteen",
    "17" to "Seventeen",
    "18" to "Eighteen",
    "19" to "Nineteen",
    "20" to "Twenty",
    "21" to "TwentyOne",
    "22" to "TwentyTwo",
    "23" to "TwentyThree",
    "24" to "TwentyFour",
    "25" to "TwentyFive",
    "26" to "TwentySix",
    "27" to "TwentySeven",
    "28" to "TwentyEight",
    "29" to "TwentyNine",
    "30" to "Thirty",
    "31" to "ThirtyOne",
    "32" to "ThirtyTwo",
    "33" to "ThirtyThree",
    "34" to "ThirtyFour",
    "35" to "ThirtyFive",
    "36" to "ThirtySix",
    "37" to "ThirtySeven",
    "38" to "ThirtyEight",
    "39" to "ThirtyNine",
    "40" to "Forty",
    "41" to "FortyOne",
    "42" to "FortyTwo",
    "43" to "FortyThree",
    "44" to "FortyFour",
    "45" to "FortyFive",
    "46" to "FortySix",
    "47" to "FortySeven",
    "48" to "FortyEight",
    "49" to "FortyNine",
    "50" to "Fifty",
    "51" to "FiftyOne",
    "52" to "FiftyTwo",
    "53" to "FiftyThree",
    "54" to "FiftyFour",
    "55" to "FiftyFive",
    "56" to "FiftySix",
    "57" to "FiftySeven",
    "58" to "FiftyEight",
    "59" to "FiftyNine",
    "60" to "Sixty",
    "61" to "SixtyOne",
    "62" to "SixtyTwo",
    "63" to "SixtyThree",
    "64" to "SixtyFour",
    "65" to "SixtyFive",
    "66" to "SixtySix",
    "67" to "SixtySeven",
    "68" to "SixtyEight",
    "69" to "SixtyNine",
    "70" to "Seventy",
    "71" to "SeventyOne",
    "72" to "SeventyTwo",
    "73" to "SeventyThree",
    "74" to "SeventyFour",
    "75" to "SeventyFive",
    "76" to "SeventySix",
    "77" to "SeventySeven",
    "78" to "SeventyEight",
    "79" to "SeventyNine",
    "80" to "Eighty",
    "81" to "EightyOne",
    "82" to "EightyTwo",
    "83" to "EightyThree",
    "84" to "EightyFour",
    "85" to "EightyFive",
    "86" to "EightySix",
    "87" to "EightySeven",
    "88" to "EightyEight",
    "89" to "EightyNine",
    "90" to "Ninety",
    "91" to "NinetyOne",
    "92" to "NinetyTwo",
    "93" to "NinetyThree",
    "94" to "NinetyFour",
    "95" to "NinetyFive",
    "96" to "NinetySix",
    "97" to "NinetySeven",
    "98" to "NinetyEight",
    "99" to "NinetyNine",
    "100" to "OneHundred",
    "101" to "OneHundredOne",
    "102" to "OneHundredTwo",
    "103" to "OneHundredThree",
    "104" to "OneHundredFour",
    "105" to "OneHundredFive",
    "106" to "OneHundredSix",
    "107" to "OneHundredSeven",
    "108" to "OneHundredEight",
    "109" to "OneHundredNine",
    "110" to "OneTen",
    "111" to "OneEleven",
    "112" to "OneTwelve",
    "113" to "OneThirteen",
    "114" to "OneFourteen",
    "115" to "OneFifteen",
    "116" to "OneSixteen",
    "117" to "OneSeventeen",
    "118" to "OneEighteen",
    "119" to "OneNineteen",
    "120" to "OneTwenty",
    "121" to "OneTwentyOne",
    "122" to "OneTwentyTwo",
    "123" to "OneTwentyThree",
    "124" to "OneTwentyFour",
    "125" to "OneTwentyFive",
    "126" to "OneTwentySix",
    "127" to "OneTwentySeven",
    "128" to "OneTwentyEight",
    "129" to "OneTwentyNine",
    "130" to "OneThirty",
    "131" to "OneThirtyOne",
    "132" to "OneThirtyTwo",
    "133" to "OneThirtyThree",
    "134" to "OneThirtyFour",
    "135" to "OneThirtyFive",
    "136" to "OneThirtySix",
    "137" to "OneThirtySeven",
    "138" to "OneThirtyEight",
    "139" to "OneThirtyNine",
    "140" to "OneForty",
    "141" to "OneFortyOne",
    "142" to "OneFortyTwo",
    "143" to "OneFortyThree",
    "144" to "OneFortyFour",
    "145" to "OneFortyFive",
    "146" to "OneFortySix",
    "147" to "OneFortySeven",
    "148" to "OneFortyEight",
    "149" to "OneFortyNine",
    "150" to "OneFifty",
    "151" to "OneFiftyOne",
    "152" to "OneFiftyTwo",
    "153" to "OneFiftyThree",
    "154" to "OneFiftyFour",
    "155" to "OneFiftyFive",
    "156" to "OneFiftySix",
    "157" to "OneFiftySeven",
    "158" to "OneFiftyEight",
    "159" to "OneFiftyNine",
    "160" to "OneSixty",
    "161" to "OneSixtyOne",
    "162" to "OneSixtyTwo",
    "163" to "OneSixtyThree",
    "164" to "OneSixtyFour",
    "165" to "OneSixtyFive",
    "166" to "OneSixtySix",
    "167" to "OneSixtySeven",
    "168" to "OneSixtyEight",
    "169" to "OneSixtyNine",
    "170" to "OneSeventy",
    "171" to "OneSeventyOne",
    "172" to "OneSeventyTwo",
    "173" to "OneSeventyThree",
    "174" to "OneSeventyFour",
    "175" to "OneSeventyFive",
    "176" to "OneSeventySix",
    "177" to "OneSeventySeven",
    "178" to "OneSeventyEight",
    "179" to "OneSeventyNine",
    "180" to "OneEighty",
    "181" to "OneEightyOne",
    "182" to "OneEightyTwo",
    "183" to "OneEightyThree",
    "184" to "OneEightyFour",
    "185" to "OneEightyFive",
    "186" to "OneEightySix",
    "187" to "OneEightySeven",
    "188" to "OneEightyEight",
    "189" to "OneEightyNine",
    "190" to "OneNinety",
    "191" to "OneNinetyOne",
    "192" to "OneNinetyTwo",
    "193" to "OneNinetyThree",
    "194" to "OneNinetyFour",
    "195" to "OneNinetyFive",
    "196" to "OneNinetySix",
    "197" to "OneNinetySeven",
    "198" to "OneNinetyEight",
    "199" to "OneNinetyNine",
    "200" to "TwoHundred",
    "201" to "TwoHundredOne",
    "202" to "TwoHundredTwo",
    "203" to "TwoHundredThree",
    "204" to "TwoHundredFour",
    "205" to "TwoHundredFive",
    "206" to "TwoHundredSix",
    "207" to "TwoHundredSeven",
    "208" to "TwoHundredEight",
    "209" to "TwoHundredNine",
    "210" to "TwoTen",
    "211" to "TwoEleven",
    "212" to "TwoTwelve",
    "213" to "TwoThirteen",
    "214" to "TwoFourteen",
    "215" to "TwoFifteen",
    "216" to "TwoSixteen",
    "217" to "TwoSeventeen",
    "218" to "TwoEighteen",
    "219" to "TwoNineteen",
    "220" to "TwoTwenty",
    "221" to "TwoTwentyOne",
    "222" to "TwoTwentyTwo",
    "223" to "TwoTwentyThree",
    "224" to "TwoTwentyFour",
    "225" to "TwoTwentyFive",
    "226" to "TwoTwentySix",
    "227" to "TwoTwentySeven",
    "228" to "TwoTwentyEight",
    "229" to "TwoTwentyNine",
    "230" to "TwoThirty",
    "231" to "TwoThirtyOne",
    "232" to "TwoThirtyTwo",
    "233" to "TwoThirtyThree",
    "234" to "TwoThirtyFour",
    "235" to "TwoThirtyFive",
    "236" to "TwoThirtySix",
    "237" to "TwoThirtySeven",
    "238" to "TwoThirtyEight",
    "239" to "TwoThirtyNine",
    "240" to "TwoForty",
    "241" to "TwoFortyOne",
    "242" to "TwoFortyTwo",
    "243" to "TwoFortyThree",
    "244" to "TwoFortyFour",
    "245" to "TwoFortyFive",
    "246" to "TwoFortySix",
    "247" to "TwoFortySeven",
    "248" to "TwoFortyEight",
    "249" to "TwoFortyNine",
    "250" to "TwoFifty",
    "251" to "TwoFiftyOne",
    "252" to "TwoFiftyTwo",
    "253" to "TwoFiftyThree",
    "254" to "TwoFiftyFour",
    "255" to "TwoFiftyFive",
    "256" to "TwoFiftySix",
    "257" to "TwoFiftySeven",
    "258" to "TwoFiftyEight",
    "259" to "TwoFiftyNine",
    "260" to "TwoSixty",
    "261" to "TwoSixtyOne",
    "262" to "TwoSixtyTwo",
    "263" to "TwoSixtyThree",
    "264" to "TwoSixtyFour",
    "265" to "TwoSixtyFive",
    "266" to "TwoSixtySix",
    "267" to "TwoSixtySeven",
    "268" to "TwoSixtyEight",
    "269" to "TwoSixtyNine",
    "270" to "TwoSeventy",
    "271" to "TwoSeventyOne",
    "272" to "TwoSeventyTwo",
    "273" to "TwoSeventyThree",
    "274" to "TwoSeventyFour",
    "275" to "TwoSeventyFive",
    "276" to "TwoSeventySix",
    "277" to "TwoSeventySeven",
    "278" to "TwoSeventyEight",
    "279" to "TwoSeventyNine",
    "280" to "TwoEighty",
    "281" to "TwoEightyOne",
    "282" to "TwoEightyTwo",
    "283" to "TwoEightyThree",
    "284" to "TwoEightyFour",
    "285" to "TwoEightyFive",
)