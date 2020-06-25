package com.drugstore.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Categories"
)
data class Category(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "children_ids") val childrenIds: String?,
    @ColumnInfo(name = "image_uri") val imageUri: String?
)