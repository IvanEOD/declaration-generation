package com.detpros.unrealkotlin.utility


/**
 *  Extensions
 *
 * @author IvanEOD ( 6/19/2023 at 2:06 PM EST )
 */

fun <T: Configuration<T, B>, B : ConfigurationBuilder<T, B>> Collection<T>.builders(): List<B> = map { it.toBuilder() }
fun <T: Configuration<T, B>, B : ConfigurationBuilder<T, B>> Collection<B>.buildAll(): List<T> = map { it.build() }
fun <T: Configuration<T, B>, B : ConfigurationBuilder<T, B>> Collection<T>.merge(): T = builders().merge().build()
fun <T: Configuration<T, B>, B : ConfigurationBuilder<T, B>> Collection<B>.merge(): B = reduce { acc, builder -> acc.include(builder) }