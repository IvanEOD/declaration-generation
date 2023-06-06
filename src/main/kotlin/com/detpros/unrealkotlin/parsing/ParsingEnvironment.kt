package com.detpros.unrealkotlin.parsing

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.extensions.ExtensionPoint
import org.jetbrains.kotlin.com.intellij.openapi.extensions.Extensions
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.openapi.util.UserDataHolderBase
import org.jetbrains.kotlin.com.intellij.pom.PomModel
import org.jetbrains.kotlin.com.intellij.pom.PomModelAspect
import org.jetbrains.kotlin.com.intellij.pom.PomTransaction
import org.jetbrains.kotlin.com.intellij.pom.impl.PomTransactionBase
import org.jetbrains.kotlin.com.intellij.pom.tree.TreeAspect
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.TreeCopyHandler
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtPsiFactory
import sun.reflect.ReflectionFactory
import java.io.File

sealed interface ParsingEnvironment : Disposable {
    val sources: Set<File>

    val environment: KotlinCoreEnvironment
    val project: MockProject
    val ktPsiFactory: KtPsiFactory
    val psiFileFactory: PsiFileFactory
    val context: ParsingContext



    companion object {
        private val instances = mutableMapOf<String, ParsingEnvironmentImpl>()
        private val sourceContexts = mutableMapOf<Set<File>, ParsingContext>()
        fun contextFor(sources: Set<File>): ParsingContext = sourceContexts.getOrPut(sources) { ParsingContext() }


        fun withSources(
            sources: Set<File>,
            context: ParsingContext = contextFor(sources),
            block: ParsingEnvironment.() -> Unit
        ) {
            val instance = instances.getOrPut(sources.joinToString("/") { it.nameWithoutExtension })  {
                ParsingEnvironmentImpl(sources, context)
            }
            instance.block()
            instance.dispose()
        }

        fun getEnvironment(file: File): ParsingEnvironment =
            instances[file.nameWithoutExtension] ?: throw IllegalStateException("Environment not initialized")

    }

}

private class ParsingEnvironmentImpl(
    override val sources: Set<File>,
    override val context: ParsingContext,
): ParsingEnvironment, Disposable by Disposer.newDisposable() {

    override val environment: KotlinCoreEnvironment by lazy {
        val compilerConfiguration = CompilerConfiguration()
        compilerConfiguration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        val environment = KotlinCoreEnvironment.createForProduction(
            this,
            compilerConfiguration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        )
        println("Sources: ${sources.size}")
        environment.addKotlinSourceRoots(sources.toList())
        environment
    }

    override val project: MockProject by lazy {
        val project = environment.project
        project as MockProject
        project.enableASTMutations()
        project
    }

    override val psiFileFactory: PsiFileFactory by lazy { PsiFileFactory.getInstance(project) }
    override val ktPsiFactory by lazy { KtPsiFactory(project, true) }

    init {
        setIdeaIoUseFallback()
    }

    private fun MockProject.enableASTMutations() {
        val extensionPoint = "org.jetbrains.kotlin.com.intellij.treeCopyHandler"
        val extensionClassName = TreeCopyHandler::class.java.name
        for (area in arrayOf(extensionArea, Extensions.getRootArea())) {
            if (!area.hasExtensionPoint(extensionPoint))
                area.registerExtensionPoint(extensionPoint, extensionClassName, ExtensionPoint.Kind.INTERFACE)
        }
        registerService(PomModel::class.java, PomModelImpl())
    }

    private class PomModelImpl : UserDataHolderBase(), PomModel {
        override fun runTransaction(transaction: PomTransaction) {
            (transaction as PomTransactionBase).run()
        }
        @Suppress("UNCHECKED_CAST", "SpreadOperator")
        override fun <T : PomModelAspect> getModelAspect(aspect: Class<T>): T? {
            if (aspect == TreeAspect::class.java) {
                val constructor = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(
                    aspect,
                    Any::class.java.getDeclaredConstructor(*arrayOfNulls<Class<*>>(0))
                )
                return constructor.newInstance() as T
            }
            return null
        }
    }

}

