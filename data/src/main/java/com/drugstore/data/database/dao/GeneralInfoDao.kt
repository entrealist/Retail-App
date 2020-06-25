package com.drugstore.data.database.dao

import androidx.room.*
import com.drugstore.data.database.entity.GeneralInfo
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GeneralInfoDao {

    @Query("SELECT registration_user_consent FROM GeneralInfo")
    abstract fun getRegistrationUserConsent(): Flow<String?>

    @Query("SELECT terms_and_conditions_url FROM GeneralInfo")
    abstract suspend fun getTermsAndConditionsUrlSuspend(): String?

    @Query("SELECT privacy_policy_url FROM GeneralInfo")
    abstract suspend fun getPrivacyPolicyUrlSuspend(): String?

    @Query("SELECT order_product_url FROM GeneralInfo")
    abstract suspend fun getOrderProductUrlSuspend(): String?

    @Query("SELECT reorder_product_url FROM GeneralInfo")
    abstract suspend fun getReorderProductUrlSuspend(): String?

    @Query("SELECT medical_record_url FROM GeneralInfo")
    abstract suspend fun getMedicalRecordUrlSuspend(): String?

    @Transaction
    open suspend fun upsertRegistrationUserConsent(registrationUserConsent: String?) {
        val id = insert(
            GeneralInfo(
                registrationUserConsent = registrationUserConsent
            )
        )
        if (id == -1L) updateRegistrationUserConsent(registrationUserConsent)
    }

    @Transaction
    open suspend fun upsertUrls(
        termsAndConditionsUrl: String?,
        privacyPolicyUrl: String?,
        orderProductUrl: String?,
        reorderProductUrl: String?,
        medicalRecordUrl: String?
    ) {
        val id = insert(
            GeneralInfo(
                termsAndConditionsUrl = termsAndConditionsUrl,
                privacyPolicyUrl = privacyPolicyUrl,
                orderProductUrl = orderProductUrl,
                reorderProductUrl = reorderProductUrl,
                medicalRecordUrl = medicalRecordUrl
            )
        )
        if (id == -1L) updateUrls(
            termsAndConditionsUrl,
            privacyPolicyUrl,
            orderProductUrl,
            reorderProductUrl,
            medicalRecordUrl
        )
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insert(generalInfo: GeneralInfo): Long

    @Query("UPDATE GeneralInfo SET registration_user_consent = :registrationUserConsent")
    internal abstract suspend fun updateRegistrationUserConsent(registrationUserConsent: String?)

    @Query("UPDATE GeneralInfo SET terms_and_conditions_url = :termsAndConditionsUrl, privacy_policy_url = :privacyPolicyUrl, order_product_url = :orderProductUrl, reorder_product_url = :reorderProductUrl, medical_record_url = :medicalRecordUrl")
    internal abstract suspend fun updateUrls(
        termsAndConditionsUrl: String?,
        privacyPolicyUrl: String?,
        orderProductUrl: String?,
        reorderProductUrl: String?,
        medicalRecordUrl: String?
    )

    @Query("DELETE FROM GeneralInfo")
    abstract suspend fun delete()
}