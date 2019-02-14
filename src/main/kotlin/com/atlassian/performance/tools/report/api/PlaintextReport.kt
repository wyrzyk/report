package com.atlassian.performance.tools.report.api

import com.atlassian.performance.tools.jiraactions.api.ActionMetricStatistics
import org.apache.commons.lang3.StringUtils.abbreviate
import java.lang.StringBuilder
import java.util.*

class PlaintextReport(
    val actionMetricStatistics: ActionMetricStatistics
) {
    fun generate(): String {
        val p95 = actionMetricStatistics.percentile(95)
        val report = StringBuilder()
        val formatter = Formatter(report)
        val lineFormat = "| %-25s | %-13s | %-8s | %-20s |\n"
        formatter.format("\n")
        formatter.format("+---------------------------+---------------+----------+----------------------+\n")
        formatter.format("| Action name               | sample size   | errors   | 95th percentile [ms] |\n")
        formatter.format("+---------------------------+---------------+----------+----------------------+\n")

        actionMetricStatistics
            .sampleSize
            .keys
            .sorted()
            .forEach { action ->
                formatter.format(
                    lineFormat,
                    abbreviate(action, 25),
                    actionMetricStatistics.sampleSize[action],
                    actionMetricStatistics.errors[action],
                    p95[action]?.toMillis()
                )
            }
        formatter.format("+---------------------------+---------------+----------+----------------------+\n")
        return report.toString()
    }
}