package com.atlassian.performance.tools.report

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import java.time.Duration

data class DurationData(
    val stats: DescriptiveStatistics,
    internal val durationMapping: (Double) -> Duration
) {

    internal companion object Factory {

        fun createEmptyMilliseconds(): DurationData {
            return DurationData(
                DescriptiveStatistics(),
                ToMilliseconds()
            )
        }

        private class ToMilliseconds : (Double) -> Duration {
            override fun invoke(milliseconds: Double): Duration {
                return Duration.ofMillis(milliseconds.toLong())
            }
        }

        fun createEmptyNanoseconds(): DurationData {
            return DurationData(
                DescriptiveStatistics(),
                ToNanoseconds()
            )
        }

        private class ToNanoseconds : (Double) -> Duration {
            override fun invoke(nanoseconds: Double): Duration {
                return Duration.ofNanos(nanoseconds.toLong())
            }
        }
    }
}
