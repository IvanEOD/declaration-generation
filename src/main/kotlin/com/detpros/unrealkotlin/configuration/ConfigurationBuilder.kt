package com.detpros.unrealkotlin.configuration


/**
 *  Configuration Builder
 *
 * @author IvanEOD ( 6/19/2023 at 1:25 PM EST )
 */
interface ConfigurationBuilder<T : Configuration<T, B>, B : ConfigurationBuilder<out T, B>> {

    fun include(other: T): B
    fun include(other: B): B

    fun build(): T

}