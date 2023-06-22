package com.detpros.unrealkotlin.corrections.models

import com.detpros.unrealkotlin.utility.Configuration
import com.detpros.unrealkotlin.utility.ConfigurationBuilder


/**
 *  Correction
 *
 * @author IvanEOD ( 6/22/2023 at 12:00 PM EST )
 */
@CorrectionMarker
interface Correction<T : Correction<T, B>, B : Correction.Builder<T, B>> : Configuration<T, B> {

    override fun toBuilder(): B


    @CorrectionBuilderMarker
    interface Builder<T : Correction<T, B>, B : Builder<T, B>>: ConfigurationBuilder<T, B> {
        override fun include(other: T): B
        override fun include(other: B): B
        override fun build(): T
    }

}
