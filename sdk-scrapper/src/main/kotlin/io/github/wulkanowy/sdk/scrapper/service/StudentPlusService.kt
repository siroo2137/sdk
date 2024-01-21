package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.conferences.Conference
import io.github.wulkanowy.sdk.scrapper.mobile.Device
import io.github.wulkanowy.sdk.scrapper.register.ContextResponse
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import retrofit2.http.GET
import retrofit2.http.Query

internal interface StudentPlusService {

    @GET("api/Context")
    suspend fun getContext(): ContextResponse

    @GET("api/Frekwencja")
    suspend fun getAttendance(
        @Query("key") key: String,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String,
    ): List<Attendance>

    @GET("api/ZarejestrowaneUrzadzenia")
    suspend fun getRegisteredDevices(): List<Device>

    @GET("api/Zebrania")
    suspend fun getConferences(): List<Conference>

    @GET("api/RealizacjaZajec")
    suspend fun getCompletedLessons(
        @Query("key") key: String,
        @Query("status") status: Int,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String,
    ): List<CompletedLesson>
}
