package com.atlassian.performance.tools.report.junit

import com.atlassian.performance.tools.io.ensureDirectory
import com.atlassian.performance.tools.io.resolveSafely
import java.nio.file.Path

interface JUnitReport {

    val testName: String
    val successful: Boolean

    fun toXml(testClassName: String): String

    fun dump(testClassName: String, path: Path) {
        val destination = path.resolveSafely(path = "$testName.xml").toFile()
        if (destination.exists()) {
            destination.delete()
        }
        destination.parentFile.ensureDirectory()
        destination.createNewFile()
        destination.writeText(toXml(testClassName))
    }
}