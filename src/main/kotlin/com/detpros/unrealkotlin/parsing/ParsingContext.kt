package com.detpros.unrealkotlin.parsing

import com.squareup.kotlinpoet.FileSpec
import org.jetbrains.kotlin.psi.KtFile
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

class ParsingContext {
    private val _ktFiles: ConcurrentLinkedDeque<KtFile> = ConcurrentLinkedDeque()
    private val _processed: ConcurrentHashMap<KtFile, FileSpec> = ConcurrentHashMap()

    fun register(file: KtFile) = _ktFiles.add(file)
    fun findKtFile(predicate: (KtFile) -> Boolean): KtFile? = _ktFiles.find(predicate)

    fun process(file: KtFile) {
        val context = ParsingVisitorContext()
        file.accept(ParsingVisitor(context))
        val fileSpec = context.files.firstOrNull() ?: throw IllegalStateException("No file spec generated for $file")
        _processed[file] = fileSpec
    }

    fun fileSpecs(): List<FileSpec> = _processed.values.toList()

}
