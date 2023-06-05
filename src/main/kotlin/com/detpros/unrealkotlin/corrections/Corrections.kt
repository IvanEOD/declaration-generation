package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.declaration.FileDeclaration

sealed class Corrections {
    abstract val environment: CorrectionEnvironment
    abstract fun correct(files: Set<FileDeclaration>)
}