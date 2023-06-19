package com.detpros.unrealkotlin.corrections.dsl

import com.detpros.unrealkotlin.configuration.ConfigurationBuilder
import com.detpros.unrealkotlin.corrections.ICorrectionConfiguration


/**
 *  Correction Configuration Builder
 *
 * @author IvanEOD ( 6/19/2023 at 11:42 AM EST )
 */
@CorrectionConfigurationMarker
sealed interface ICorrectionConfigurationBuilder<T : ICorrectionConfiguration<T, B>, B : ICorrectionConfigurationBuilder<T, B>>: ConfigurationBuilder<T, B> {

    override fun include(other: T): B
    override fun include(other: B): B
    override fun build(): T

}

