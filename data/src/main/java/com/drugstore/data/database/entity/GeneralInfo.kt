package com.drugstore.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "GeneralInfo"
)
class GeneralInfo(
    @ColumnInfo(name = "registration_user_consent") var registrationUserConsent: String? = null,
    @ColumnInfo(name = "terms_and_conditions_url") var termsAndConditionsUrl: String? = null,
    @ColumnInfo(name = "privacy_policy_url") var privacyPolicyUrl: String? = null,
    @ColumnInfo(name = "order_product_url") var orderProductUrl: String? = null,
    @ColumnInfo(name = "reorder_product_url") var reorderProductUrl: String? = null,
    @ColumnInfo(name = "medical_record_url") var medicalRecordUrl: String? = null
) {

    @PrimaryKey
    var lock: Int = 1
        set(@Suppress("UNUSED_PARAMETER") value) { field = 1 }
}