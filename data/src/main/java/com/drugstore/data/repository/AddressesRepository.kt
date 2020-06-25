package com.drugstore.data.repository

import androidx.lifecycle.MutableLiveData
import com.drugstore.data.database.dao.AddressesDao
import com.drugstore.data.repository.core.*
import com.drugstore.data.webservice.customer.CustomerService
import com.drugstore.data.webservice.customer.request.PutDeliveryAddressRequest
import com.drugstore.data.webservice.exception.JsonDataException
import com.drugstore.domain.entity.Address
import com.drugstore.domain.entity.Address.Type.DHL_PACKSTATION
import com.drugstore.domain.entity.Address.Type.REGULAR
import com.drugstore.domain.entity.Region
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import org.threeten.bp.Duration
import java.util.Locale.GERMANY
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressesRepository @Inject constructor(
    private val cacheRecordsRepository: CacheRecordsRepository,
    private val addressesDao: AddressesDao,
    private val customerService: CustomerService,
    private val regionsRepository: RegionsRepository,
    private val userRepository: UserRepository
) {

    fun getAddresses(): Resource<List<Address>> {
        val fetchState = MutableLiveData<JobState?>(
            Completed
        )

        val refresh = suspend {
            try {
                fetchState.value = Active
                refreshAddresses()
                fetchState.value = Completed
            } catch (e: Exception) {
                fetchState.value =
                    Cancelled(e)
                throw e
            }
        }

        val update = suspend {
            if (cacheRecordsRepository.isCacheExpired(KEY_ADDRESSES)) refresh()
        }

        return Resource(
            addressesDao.get(),
            fetchState,
            refresh,
            update
        )
    }

    internal suspend fun refreshAddresses() = withContext(Default) {
        val userId = userRepository.getId()
        val response = call {
            customerService.getDeliveryAddress(userId)
        }

        val countries = regionsRepository.getCountries()
        val addresses = response.map { address ->
            val country = countries.find { it.id == address.countryId }
                ?: throw JsonDataException()
                    .map()
            address.map(country)
        }

        addressesDao.deleteAndInsert(addresses)
        cacheRecordsRepository.setCacheExpiration(
            KEY_ADDRESSES,
            LIFESPAN_ADDRESSES
        )
    }

    fun getAddress(id: Int) = addressesDao.get(id)

    fun getCountryAddressTypes(countryId: String?) = when(countryId) {
        GERMANY.country -> listOf(REGULAR, DHL_PACKSTATION)
        else -> listOf(REGULAR)
    }

    suspend fun updateAddress(
        addressType: Address.Type, title: String?, firstName: String?, lastName: String?,
        country: Region.Country?, postalCode: String?, city: String?, street: String?, houseNumber: String?,
        companyName: String?, packstationAddress: String?, packstationNumber: String?, phoneNumber: String?
    ) = withContext(Default) {
        val request =
            PutDeliveryAddressRequest(
                addressType.reverseMap(),
                title,
                firstName,
                lastName,
                country?.id,
                postalCode,
                city,
                if (addressType == REGULAR) street else null,
                if (addressType == REGULAR) houseNumber else null,
                if (addressType == REGULAR) companyName else null,
                if (addressType == DHL_PACKSTATION) packstationAddress else null,
                if (addressType == DHL_PACKSTATION) packstationNumber else null,
                phoneNumber
            )
        val userId = userRepository.getId()
        val response = call {
            customerService.putDeliveryAddress(
                userId,
                request
            )
        }

        val countries = regionsRepository.getCountries()
        val country = countries.find { it.id == response.address.countryId }
            ?: throw JsonDataException().map()
        val address = response.address.map(country)
        addressesDao.insert(address)

        return@withContext response.message
    }

    companion object {
        private const val KEY_ADDRESSES = "addresses"
        private val LIFESPAN_ADDRESSES: Duration = Duration.ofMinutes(10)
    }
}