package com.atlassian.performance.tools.report.jstat

import com.atlassian.performance.tools.infrastructure.api.metric.Dimension
import com.atlassian.performance.tools.infrastructure.api.metric.SystemMetric
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.InputStream
import java.time.OffsetDateTime

internal class JstatGcutilParser {
    internal fun parse(
        inputStream: InputStream,
        system: String
    ): List<SystemMetric> {
        val parser = CSVParser(
            inputStream.bufferedReader(),
            CSVFormat.DEFAULT.withHeader(JstatGcutilHeader::class.java)
        )
        val sortedJstatMetrics = parser
            .toList()
            .map { record -> JstatMetric(record, system) }
            .sortedBy { it.start }

        var last: CSVRecord? = null
        return sortedJstatMetrics.flatMap {
            val systemMetrics = it.toSystemMetrics(last)
            last = it.record
            systemMetrics
        }
    }

    private class JstatMetric(
        val record: CSVRecord,
        private val system: String
    ) {
        val start = OffsetDateTime.parse(record.get(JstatGcutilHeader.DATE)).toInstant()!!

        fun toSystemMetrics(
            previous: CSVRecord?
        ): List<SystemMetric> {
            // here
            return listOf(
                metric(
                    dimension = Dimension.JSTAT_SURVI_0,
                    value = convert(record, JstatGcutilHeader.S0)
                ),
                metric(
                    dimension = Dimension.JSTAT_SURVI_1,
                    value = convert(record, JstatGcutilHeader.S1)
                ),
                metric(
                    dimension = Dimension.JSTAT_EDEN,
                    value = convert(record, JstatGcutilHeader.E)
                ),
                metric(
                    dimension = Dimension.JSTAT_OLD,
                    value = convert(record, JstatGcutilHeader.O)
                ),
                metric(
                    dimension = Dimension.JSTAT_META,
                    value = convert(record, JstatGcutilHeader.M)
                ),
                metric(
                    dimension = Dimension.JSTAT_COMPRESSED_CLASS,
                    value = convert(record, JstatGcutilHeader.CCS)
                ),
                metric(
                    dimension = Dimension.JSTAT_YOUNG_GEN_GC,
                    value = convert(record, JstatGcutilHeader.YGC) -
                        convert(previous, JstatGcutilHeader.YGC)
                ),
                metric(
                    dimension = Dimension.JSTAT_YOUNG_GEN_GC_TIME,
                    value = convert(record, JstatGcutilHeader.YGCT) -
                        convert(previous, JstatGcutilHeader.YGCT)
                ),
                metric(
                    dimension = Dimension.JSTAT_FULL_GC,
                    value = convert(record, JstatGcutilHeader.FGC) -
                        convert(previous, JstatGcutilHeader.FGC)
                ),
                metric(
                    dimension = Dimension.JSTAT_FULL_GC_TIME,
                    value = convert(record, JstatGcutilHeader.FGCT) -
                        convert(previous, JstatGcutilHeader.FGCT)
                ),
                metric(
                    dimension = Dimension.JSTAT_TOTAL_GC_TIME,
                    value = convert(record, JstatGcutilHeader.GCT)
                )
            )
        }

        private fun convert(record: CSVRecord?,
                            header: JstatGcutilHeader): Double {
            try {
                val result = record?.get(header)?.toDouble() ?: 0.0
                return result
            } catch (exception: NumberFormatException) {
                return 0.0
            }
        }

        private fun metric(
            dimension: Dimension,
            value: Double
        ): SystemMetric {
            return SystemMetric(
                start = start,
                dimension = dimension,
                value = value,
                system = system
            )
        }
    }
}