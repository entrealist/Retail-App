package com.drugstore.app.main.categories.products

import android.app.Application
import android.view.View
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.drugstore.R
import com.drugstore.app.core.handle
import com.drugstore.app.core.item.ActionItem
import com.drugstore.data.repository.CatalogRepository
import com.drugstore.data.repository.core.Active
import com.drugstore.data.repository.core.Cancelled
import com.drugstore.domain.entity.Product
import com.kasprov.android.core.lifecycle.BaseViewModel
import com.kasprov.android.core.lifecycle.combine
import com.kasprov.android.core.lifecycle.set
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProductsVM @ViewModelInject internal constructor(
    application: Application,
    @Assisted handle: SavedStateHandle,
    catalogRepository: CatalogRepository
) : BaseViewModel() {
    private val categoryId: Int = handle["categoryId"] ?: throw IllegalArgumentException("No categoryId provided")
    private val productsResource = catalogRepository.getProductsByCategory(categoryId)
    val products = productsResource.resource.asLiveData()
    private val productsFetchState = productsResource.fetchState

    private val productsRetryItem = ActionItem(
        application.getString(R.string.products_retry_update_products_title),
        application.getString(R.string.products_retry_update_products_action),
        View.OnClickListener { refresh() }
    )

    val productsFooterItems = products.combine(productsFetchState) { products, fetchState ->
        when {
            fetchState is Cancelled && products.isEmpty() -> listOf(productsRetryItem)
            else -> null
        }
    }
    val isProductsRefreshingIndicatorVisible = productsFetchState.map { it is Active }

    private var updateProductsJob: Job? = null

    internal fun onStart() {
        if (updateProductsJob?.isCompleted == false) return
        updateProductsJob = viewModelScope.launch {
            try {
                productsResource.update()
            } catch (e: Exception) {
                e.handle(navigateTo)
            }
        }
    }

    internal fun refresh() {
        if (updateProductsJob?.isCompleted == false) return
        updateProductsJob = viewModelScope.launch {
            try {
                productsResource.refresh()
            } catch (e: Exception) {
                e.handle(navigateTo)
            }
        }
    }

    fun product(product: Product) = navigateTo.set(ProductsFragmentDirections.actionProductsToProduct(product.title, product.id))
}