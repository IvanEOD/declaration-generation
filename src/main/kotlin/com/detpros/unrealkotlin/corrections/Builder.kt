package com.detpros.unrealkotlin.corrections


/**
 *  Builder
 *
 * @author IvanEOD ( 6/22/2023 at 1:44 PM EST )
 */


fun unrealDeclarationCorrections(block: UnrealDeclarationsCorrection.Builder.() -> Unit) =
    UnrealDeclarationsCorrection.Builder().apply(block).build()