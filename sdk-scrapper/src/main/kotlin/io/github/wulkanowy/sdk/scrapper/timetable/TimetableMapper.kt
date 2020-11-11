package io.github.wulkanowy.sdk.scrapper.timetable

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.toDate
import io.github.wulkanowy.sdk.scrapper.toFormat
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import org.jsoup.Jsoup
import java.time.LocalDate

private val parser = TimetableParser()

fun TimetableResponse.mapTimetableList(startDate: LocalDate, endDate: LocalDate?) = rows.flatMap { lessons ->
    lessons.drop(1).mapIndexed { i, it ->
        val times = lessons[0].split("<br />")
        val date = headers.union(_headersOld).drop(1)[i].date.split("<br />")[1].toDate("dd.MM.yyyy")
        TimetableCell(
            date = date,
            start = "${date.toLocalDate().toFormat("yyyy-MM-dd")} ${times[1]}".toDate("yyyy-MM-dd HH:mm"),
            end = "${date.toLocalDate().toFormat("yyyy-MM-dd")} ${times[2]}".toDate("yyyy-MM-dd HH:mm"),
            number = times[0].toInt(),
            td = Jsoup.parse(it)
        )
    }.mapNotNull { parser.getTimetable(it) }
}.asSequence().filter {
    it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= endDate ?: startDate.plusDays(4)
}.sortedWith(compareBy({ it.date }, { it.number })).toList()

fun TimetableResponse.mapTimetableAdditional() = additional.flatMap { day ->
    val date = day.header.substringAfter(", ").toDate("dd.MM.yyyy")
    day.descriptions.map { lesson ->
        val startTime = lesson.description.substringBefore(" - ")
        val endTime = lesson.description.split(" ")[2]
        Timetable(
            number = -1,
            date = date,
            start = "${date.toLocalDate().toFormat("yyyy-MM-dd")} $startTime".toDate("yyyy-MM-dd HH:mm"),
            end = "${date.toLocalDate().toFormat("yyyy-MM-dd")} $endTime".toDate("yyyy-MM-dd HH:mm"),
            subject = lesson.description.substringAfter("$endTime ")
        )
    }
}

fun ApiResponse<*>.mapCompletedLessonsList(start: LocalDate, endDate: LocalDate?, moshi: Moshi.Builder): List<CompletedLesson> {
    val type = Types.newParameterizedType(MutableMap::class.java, String::class.java, List::class.java)
    val adapter = moshi.build().adapter<Map<String, List<Map<String, Any>>>>(type)

    val mapType = Types.newParameterizedType(List::class.java, MutableMap::class.java)
    val mapAdapter = moshi.build().adapter<List<Map<String, Any>>>(mapType)

    val lessonType = Types.newParameterizedType(List::class.java, CompletedLesson::class.java)
    val completedLessonAdapter = moshi.build().adapter<List<CompletedLesson>>(lessonType)
    return adapter.fromJsonValue(data).orEmpty()
        .map { it.value }
        .map { mapAdapter.toJson(it) }
        .flatMap { completedLessonAdapter.fromJson(it).orEmpty() }
        .map {
            it.apply {
                teacherSymbol = teacher.substringAfter(" [").substringBefore("]")
                teacher = teacher.substringBefore(" [")
            }
        }.sortedWith(compareBy({ it.date }, { it.number })).toList().filter {
            it.date.toLocalDate() >= start && it.date.toLocalDate() <= endDate ?: start.plusDays(4)
        }
}
