package com.drugstore.data.repository

import androidx.lifecycle.MutableLiveData
import com.drugstore.data.database.dao.OrdersDao
import com.drugstore.data.repository.core.*
import com.drugstore.data.webservice.customer.CustomerService
import com.drugstore.domain.entity.OrderDomain
import com.drugstore.domain.map
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.threeten.bp.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrdersRepository @Inject constructor(
    private val cacheRecordsRepository: CacheRecordsRepository,
    private val ordersDao: OrdersDao,
    private val customerService: CustomerService,
    private val userRepository: UserRepository
) {

    fun getOrdersWithProducts(): Resource<List<OrderDomain>> {
        val list = ordersDao.getWithProducts().map { ordersWithProducts ->
            ordersWithProducts.map { it.map() }
        }

        val fetchState = MutableLiveData<JobState?>(
            Completed
        )

        val refresh = suspend {
            try {
                fetchState.value = Active
                refreshOrders()
                fetchState.value = Completed
            } catch (e: Exception) {
                fetchState.value =
                    Cancelled(e)
                throw e
            }
        }

        val update = suspend {
            if (cacheRecordsRepository.isCacheExpired(KEY_ORDERS)) refresh()
        }

        return Resource(
            list,
            fetchState,
            refresh,
            update
        )
    }

    internal suspend fun refreshOrders() = withContext(Default) {
        val userId = userRepository.getId()
        val response = call {
            customerService.getOrderIndex(userId)
        }

        val orders = response.map { it.map(userId) }

        val orderProducts = response
            .map { order ->
                order.products.map { it.map(order.id) }
            }
            .flatten()

        ordersDao.deleteAndInsert(orders, orderProducts)
        cacheRecordsRepository.setCacheExpiration(
            KEY_ORDERS,
            LIFESPAN_ORDERS
        )
    }

    companion object {
        private const val KEY_ORDERS = "orders"
        private val LIFESPAN_ORDERS: Duration = Duration.ofMinutes(10)
    }
}