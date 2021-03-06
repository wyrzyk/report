package com.atlassian.performance.tools.report.api.judge

import com.atlassian.performance.tools.report.api.PerformanceCriteria
import com.atlassian.performance.tools.report.api.result.InteractionStats

/**
 * Judges an experiment cohort in relation to a baseline cohort.
 */
class BaselineComparingJudge {

    fun judge(
        performanceCriteria: PerformanceCriteria,
        baselineStats: InteractionStats,
        experimentStats: InteractionStats
    ): Verdict {
        return RelativeTypicalPerformanceJudge().judge(performanceCriteria.getCenterCriteria(), baselineStats, experimentStats) +
            RelativePerformanceStabilityJudge().judge(performanceCriteria.getDispersionCriteria(), baselineStats, experimentStats)
    }
}