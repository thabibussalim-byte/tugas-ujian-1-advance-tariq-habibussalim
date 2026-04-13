package com.example.blusq.data.local.entity

import androidx.room.*

@Entity(tableName = "event")
data class EventEntity(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "summary")
    val summary: String?,

    @ColumnInfo(name = "ownerName")
    val ownerName: String?,

    @ColumnInfo(name = "cityName")
    val cityName: String?,

    @ColumnInfo(name = "beginTime")
    val beginTime: String?,

    @ColumnInfo(name = "endTime")
    val endTime: String?,

    @ColumnInfo(name = "category")
    val category: String?,

    @ColumnInfo(name = "imageLogo")
    val imageLogo: String?,

    @ColumnInfo(name = "mediaCover")
    val mediaCover: String?,

    @ColumnInfo(name = "link")
    val link: String?,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "registrants")
    val registrants: Int?,

    @ColumnInfo(name = "quota")
    val quota: Int?,

    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean? = false,

    @ColumnInfo(name = "isFinished")
    var isFinished: Boolean? = false
)