package io.github.wulkanowy.sdk.hebe.repository

import io.github.wulkanowy.sdk.hebe.getEnvelopeOrThrowError
import io.github.wulkanowy.sdk.hebe.models.Grade
import io.github.wulkanowy.sdk.hebe.models.GradeAverage
import io.github.wulkanowy.sdk.hebe.models.GradeSummary
import io.github.wulkanowy.sdk.hebe.service.StudentService

internal class StudentRepository(private val studentService: StudentService) {

    suspend fun getGrades(pupilId: Int, periodId: Int): List<Grade> {
        return studentService.getGrades(
            pupilId = pupilId,
            periodId = periodId,
        ).getEnvelopeOrThrowError().orEmpty()
    }

    suspend fun getGradesSummary(pupilId: Int, periodId: Int): List<GradeSummary> {
        return studentService.getGradesSummary(
            pupilId = pupilId,
            periodId = periodId,
        ).getEnvelopeOrThrowError().orEmpty()
    }

    suspend fun getGradesAverage(pupilId: Int, periodId: Int): List<GradeAverage> {
        return studentService.getGradesAverage(
            pupilId = pupilId,
            periodId = periodId,
        ).getEnvelopeOrThrowError().orEmpty()
    }
}
