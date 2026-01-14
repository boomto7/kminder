package com.kminder.data.local.model

import androidx.room.ColumnInfo

/**
 * 일별 감정 통계 집계 결과 (Tuple)
 */
data class DailyEmotionStatisticTuple(
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "entryCount") val entryCount: Int,
    @ColumnInfo(name = "avgJoy") val avgJoy: Float?,
    @ColumnInfo(name = "avgTrust") val avgTrust: Float?,
    @ColumnInfo(name = "avgFear") val avgFear: Float?,
    @ColumnInfo(name = "avgSurprise") val avgSurprise: Float?,
    @ColumnInfo(name = "avgSadness") val avgSadness: Float?,
    @ColumnInfo(name = "avgDisgust") val avgDisgust: Float?,
    @ColumnInfo(name = "avgAnger") val avgAnger: Float?,
    @ColumnInfo(name = "avgAnticipation") val avgAnticipation: Float?
)
