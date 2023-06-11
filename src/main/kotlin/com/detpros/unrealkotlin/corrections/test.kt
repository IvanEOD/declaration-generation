package com.detpros.unrealkotlin.corrections

import com.detpros.unrealkotlin.declaration.PackageDeclaration
import com.detpros.unrealkotlin.declaration.toDeclaration
import com.detpros.unrealkotlin.parsing.ParsingContext
import com.detpros.unrealkotlin.parsing.ParsingEnvironment
import com.squareup.kotlinpoet.FileSpec
import kotlinx.coroutines.*
import org.jetbrains.kotlin.psi.KtFile
import java.io.File


lateinit var context: ParsingContext



fun main() {

    val sourceDir = File("C:\\Users\\ivanc\\OneDrive\\Desktop\\unreal-kotlin\\build\\.unreal-kotlin\\generated\\dukat")
//    val sourceDir = File("G:\\Games\\UnrealKt\\unreal-kotlin\\build\\.unreal-kotlin\\generated\\dukat")
    val sourceFiles = sourceDir.listFiles()!!.filter { it.extension == "kt" }.toSet()
    val outputDirectory = File("src/main/resources")

    context = ParsingEnvironment.contextFor(sourceFiles)

    ParsingEnvironment.withSources(sourceFiles) {
        val queue = FileReformatProcessorImpl()
        val ktFiles = environment.getSourceFiles()
        queue.init()
        ktFiles.forEach { file ->
            context.register(file)
            queue.submit(file.name)
        }
        queue.await()
    }

    val packageContext = PackageDeclaration.withSources("ue", context.fileSpecs().map(FileSpec::toDeclaration).toSet())

    val environment = CorrectionEnvironment(outputDirectory, packageContext, CorrectionConfiguration.Default)
    environment.process()

    
}

abstract class FileReformatProcessor {
    abstract fun init()
    abstract fun submit(fileName: String)
    abstract fun await()
}

private class FileReformatProcessorImpl : FileReformatProcessor() {
    private val queue = mutableListOf<Job>()

    override fun init() {}

    @OptIn(DelicateCoroutinesApi::class)
    override fun submit(fileName: String) {
        queue.add(GlobalScope.launch { ProcessFileReformatImpl(fileName).process() })
    }

    override fun await() {
        runBlocking { queue.joinAll() }
    }

}


abstract class ProcessFileReformat  {
    private lateinit var source: KtFile
    lateinit var fileName: String

    fun process() {
        try {
            source = context.findKtFile { it.name == fileName } ?: return
            context.process(source)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

}

private class ProcessFileReformatImpl(
    fileName: String
) : ProcessFileReformat() {
    init {
        this.fileName = fileName
    }
}