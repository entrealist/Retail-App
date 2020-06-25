package com.drugstore.data.database

import androidx.room.TypeConverter
import com.drugstore.domain.entity.Address
import com.drugstore.domain.entity.ContactDetail
import com.drugstore.domain.entity.Gender
import org.jetbrains.annotations.Contract
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset

class Converters {

    companion object {

        private val offsetNow: ZoneOffset = OffsetDateTime.now().offset

        @Contract("null -> null")
        @TypeConverter
        @JvmStatic
        fun LocalDateTime?.fromLocalDateTime() = this?.toEpochSecond(offsetNow)

        @Contract("null -> null; !null -> !null")
        @TypeConverter
        @JvmStatic
        fun Long?.toLocalDateTime() = this?.toLocalDateTime()

        fun Long.toLocalDateTime(): LocalDateTime =
            LocalDateTime.ofEpochSecond(this, 0,
                offsetNow
            )

        @Contract("null -> null")
        @TypeConverter
        @JvmStatic
        fun LocalDate?.fromLocalDate() = this?.atStartOfDay()?.fromLocalDateTime()

        @Contract("null -> null; !null -> !null")
        @TypeConverter
        @JvmStatic
        fun toLocalDate(epochSecond: Long?) = epochSecond?.toLocalDateTime()?.toLocalDate()


        @Contract("null -> null")
        @TypeConverter
        @JvmStatic
        fun List<String>?.fromStringList() = this?.joinToString(",")

        @Contract("null -> null; !null -> !null")
        @TypeConverter
        @JvmStatic
        fun String?.toStringList() = this?.split(",")


        @Contract("null -> null")
        @TypeConverter
        @JvmStatic
        fun Int?.toGender() = this?.let { Gender.getById(it) }

        @Contract("null -> null; !null -> !null")
        @TypeConverter
        @JvmStatic
        fun Gender?.fromGender() = this?.id


        @Contract("null -> null")
        @TypeConverter
        @JvmStatic
        fun Int?.toContactDetailsType() = this?.let { ContactDetail.Type.getById(it) }

        @Contract("null -> null; !null -> !null")
        @TypeConverter
        @JvmStatic
        fun ContactDetail.Type?.fromContactDetailsType() = this?.id

        @Contract("null -> null")
        @TypeConverter
        @JvmStatic
        fun Int?.toAddressType() = this?.let { Address.Type.getById(it) }

        @Contract("null -> null; !null -> !null")
        @TypeConverter
        @JvmStatic
        fun Address.Type?.fromAddressType() = this?.id
    }
}