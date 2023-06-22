package com.detpros.unrealkotlin.corrections.models

import com.detpros.unrealkotlin.corrections.CorrectionEnvironment
import com.detpros.unrealkotlin.declaration.FileDeclaration


/**
 *  Corrections
 *
 * @author IvanEOD ( 6/22/2023 at 12:29 PM EST )
 */
abstract class Corrections {
    abstract val environment: CorrectionEnvironment
    abstract fun correct(files: Set<FileDeclaration>)
}