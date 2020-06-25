package com.drugstore.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Products",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Product(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "category_id", index = true) val categoryId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "image_uri") val imageUri: String?,
    @ColumnInfo(name = "children_image_uris") val childrenImageUris: List<String>
)