package com.drugstore.data.repository

import androidx.lifecycle.MutableLiveData
import com.drugstore.data.BuildConfig
import com.drugstore.data.database.dao.ContactDetailsDao
import com.drugstore.data.database.dao.GeneralInfoDao
import com.drugstore.data.repository.core.*
import com.drugstore.data.repository.entity.Request
import com.drugstore.data.webservice.auth.AuthService
import com.drugstore.data.webservice.front.FrontService
import com.drugstore.data.webservice.front.request.PostContactUsRequest
import com.drugstore.domain.entity.ContactDetail
import com.drugstore.domain.entity.Order
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import org.threeten.bp.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonRepository @Inject constructor(
    private val cacheRecordsRepository: CacheRecordsRepository,
    private val contactDetailsDao: ContactDetailsDao,
    private val generalInfoDao: GeneralInfoDao,
    private val authService: AuthService,
    private val frontService: FrontService
) {

    fun getContactDetails(): Resource<List<ContactDetail>> {
        val fetchState = MutableLiveData<JobState?>(
            Completed
        )

        val refresh = suspend {
            try {
                fetchState.value = Active
                refreshLegalInfo()
                fetchState.value = Completed
            } catch (e: Exception) {
                fetchState.value =
                    Cancelled(e)
                throw e
            }
        }

        val update = suspend {
            if (cacheRecordsRepository.isCacheExpired(KEY_LEGAL_INFO)) refresh()
        }

        return Resource(
            contactDetailsDao.get(),
            fetchState,
            refresh,
            update
        )
    }

    fun getRegistrationUserConsent(): Resource<String?> {
        val fetchState = MutableLiveData<JobState?>(
            Completed
        )

        val refresh = suspend {
            try {
                fetchState.value = Active
                refreshLegalInfo()
                fetchState.value = Completed
            } catch (e: Exception) {
                fetchState.value =
                    Cancelled(e)
                throw e
            }
        }

        val update = suspend {
            if (cacheRecordsRepository.isCacheExpired(KEY_LEGAL_INFO)) refresh()
        }

        return Resource(
            generalInfoDao.getRegistrationUserConsent(),
            fetchState,
            refresh,
            update
        )
    }

    internal suspend fun refreshLegalInfo() = withContext(Default) {
        val response =
            call { frontService.getStaticInfoLegal() }
        contactDetailsDao.deleteAndInsert(response.mapContactDetails())
        generalInfoDao.upsertRegistrationUserConsent(response.agreeDescription)
        cacheRecordsRepository.setCacheExpiration(
            KEY_LEGAL_INFO,
            LIFESPAN_LEGAL_INFO
        )
    }

    suspend fun getTermsAndConditionsRequest() = withContext(Default) {
        updateLinksInfo()
        generalInfoDao.getTermsAndConditionsUrlSuspend()?.run {
            val response =
                call { authService.postFront() }
            Request(
                "$this?token=${response.token}",
                BuildConfig.FRONT_HEADERS
            )
        }
    }

    suspend fun getPrivacyPolicyRequest() = withContext(Default) {
        updateLinksInfo()
        generalInfoDao.getPrivacyPolicyUrlSuspend()?.run {
            val response =
                call { authService.postFront() }
            Request(
                "$this?token=${response.token}",
                BuildConfig.FRONT_HEADERS
            )
        }
    }

    suspend fun getOrderRequest(productId: Int, productOneId: Int) = withContext(Default) {
        updateLinksInfo()
        generalInfoDao.getOrderProductUrlSuspend()?.run {
            val response =
                call { authService.postFront() }
            Request(
                "$this?token=${response.token}&catalog_id=$productId&item_id=$productOneId",
                BuildConfig.FRONT_HEADERS
            )
        }
    }

    suspend fun getOrderPrescriptionRequest(order: Order) = withContext(Default) {
        order.prescriptionUrl.run {
            val response =
                call { authService.postFront() }
            Request(
                "$this?token=${response.token}",
                BuildConfig.FRONT_HEADERS
            )
        }
    }

    suspend fun getOrderDetailsRequest(order: Order) = withContext(Default) {
        order.orderUrl.run {
            val response =
                call { authService.postFront() }
            Request(
                "$this?token=${response.token}",
                BuildConfig.FRONT_HEADERS
            )
        }
    }

    suspend fun getReorderRequest(order: Order, isSameDosage: Boolean) = withContext(Default) {
        updateLinksInfo()
        generalInfoDao.getReorderProductUrlSuspend()?.run {
            val response =
                call { authService.postFront() }
            val token = "token=${response.token}"
            val orderId = "order_id=${order.id}"
            val dosage = if (isSameDosage) "dosage=same" else "dosage=other"
            Request(
                "$this?$token&$orderId&$dosage",
                BuildConfig.FRONT_HEADERS
            )
        }
    }

    suspend fun getMedicalRecordRequest() = withContext(Default) {
        updateLinksInfo()
        generalInfoDao.getMedicalRecordUrlSuspend()?.run {
            val response =
                call { authService.postFront() }
            val token = "token=${response.token}"
            Request(
                "$this?$token",
                BuildConfig.FRONT_HEADERS
            )
        }
    }

    private suspend fun updateLinksInfo() = withContext(Default) {
        if (cacheRecordsRepository.isCacheFresh(KEY_LINKS_INFO)) return@withContext

        val staticInfoLinksResponse =
            call { frontService.getStaticInfoLinks() }
        val staticInfoCommonResponse =
            call { frontService.getStaticInfoCommon() }
        generalInfoDao.upsertUrls(
            staticInfoLinksResponse.termsAndConditions.addSchemeIfMissing(),
            staticInfoLinksResponse.privacy.addSchemeIfMissing(),
            staticInfoCommonResponse.addToCard.addSchemeIfMissing(),
            staticInfoCommonResponse.startReorder.addSchemeIfMissing(),
            staticInfoCommonResponse.prescriptionDefaults.addSchemeIfMissing()
        )
        cacheRecordsRepository.setCacheExpiration(
            KEY_LINKS_INFO,
            LIFESPAN_LINKS_INFO
        )
    }

    suspend fun contactUs(email: String?, name: String?, details: String?) = withContext(Default) {
        val request =
            PostContactUsRequest(
                details,
                email,
                name
            )
        val response = com.drugstore.data.repository.call {
            frontService.postContactUs(request)
        }
        return@withContext response.message
    }

    companion object {
        const val KEY_LEGAL_INFO = "legal_info"
        private val LIFESPAN_LEGAL_INFO: Duration = Duration.ofHours(2)

        const val KEY_LINKS_INFO = "links_info"
        private val LIFESPAN_LINKS_INFO: Duration = Duration.ofHours(2)
    }
}