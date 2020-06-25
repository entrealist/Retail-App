package com.drugstore.app.main.orders

import android.app.Application
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drugstore.CoreGraphDirections
import com.drugstore.R
import com.drugstore.app.core.handle
import com.drugstore.app.core.item.ActionItem
import com.drugstore.data.repository.CommonRepository
import com.drugstore.data.repository.OrdersRepository
import com.drugstore.data.repository.core.Active
import com.drugstore.data.repository.core.Cancelled
import com.drugstore.domain.entity.OrderDomain
import com.kasprov.android.core.lifecycle.BaseViewModel
import com.kasprov.android.core.lifecycle.combine
import com.kasprov.android.core.lifecycle.set
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OrdersVM @ViewModelInject internal constructor(
    application: Application,
    private val commonRepository: CommonRepository,
    ordersRepository: OrdersRepository
) : BaseViewModel() {
    private val ordersResource = ordersRepository.getOrdersWithProducts()
    val orders = ordersResource.resource.asLiveData()
    private val ordersFetchState = ordersResource.fetchState

    private val ordersRetryItem = ActionItem(
        application.getString(R.string.orders_retry_update_orders_title),
        application.getString(R.string.orders_retry_update_orders_action),
        View.OnClickListener { refresh() }
    )

    val ordersFooterItems = orders.combine(ordersFetchState) { orders, fetchState ->
        when {
            fetchState is Cancelled && orders.isEmpty() -> listOf(ordersRetryItem)
            else -> null
        }
    }
    val isOrdersRefreshingIndicatorVisible = ordersFetchState.map { it is Active }

    private val _isLoading = MutableLiveData<Boolean?>(false)
    val isLoadingIndicatorVisible: LiveData<Boolean?> = _isLoading

    private var updateOrdersJob: Job? = null
    private var getRequestJob: Job? = null

    internal fun onStart() {
        if (updateOrdersJob?.isCompleted == false) return
        updateOrdersJob = viewModelScope.launch {
            try {
                ordersResource.update()
            } catch (e: Exception) {
                e.handle(navigateTo)
            }
        }
    }

    internal fun refresh()  {
        if (updateOrdersJob?.isCompleted == false) return
        updateOrdersJob = viewModelScope.launch {
            try {
                ordersResource.refresh()
            } catch (e: Exception) {
                e.handle(navigateTo)
            }
        }
    }

    fun prescription(order: OrderDomain) {
        if (updateOrdersJob?.isCompleted == false) return
        if (getRequestJob?.isCompleted == false) return
        getRequestJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                commonRepository.getOrderPrescriptionRequest(order.order).let {
                    navigateTo.set(CoreGraphDirections.actionToWebView(it))
                }
            } catch (e: Exception) {
                e.handle(navigateTo)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun details(order: OrderDomain) {
        if (updateOrdersJob?.isCompleted == false) return
        if (getRequestJob?.isCompleted == false) return
        getRequestJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                commonRepository.getOrderDetailsRequest(order.order).let {
                    navigateTo.set(CoreGraphDirections.actionToWebView(it))
                }
            } catch (e: Exception) {
                e.handle(navigateTo)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reorderOther(order: OrderDomain) = reorder(order, false)
    fun reorderSame(order: OrderDomain) = reorder(order, true)
    private fun reorder(order: OrderDomain, isSameDosage: Boolean) {
        if (updateOrdersJob?.isCompleted == false) return
        if (getRequestJob?.isCompleted == false) return
        getRequestJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                commonRepository.getReorderRequest(order.order, isSameDosage)?.let {
                    navigateTo.set(CoreGraphDirections.actionToWebView(it))
                }
            } catch (e: Exception) {
                e.handle(navigateTo)
            } finally {
                _isLoading.value = false
            }
        }
    }
}