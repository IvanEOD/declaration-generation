package com.detpros.unrealkotlin.configuration


/**
 *  Configuration
 *
 * @author IvanEOD ( 6/19/2023 at 1:23 PM EST )
 */
interface Configuration<T : Configuration<T, out B>, B : ConfigurationBuilder<out T, out B>> {
    fun toBuilder(): B
}

