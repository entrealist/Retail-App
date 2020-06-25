package com.drugstore.app.main.categories

import android.app.Application
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.drugstore.R
import com.drugstore.app.core.handle
import com.drugstore.app.core.item.ActionItem
import com.drugstore.data.repository.CatalogRepository
import com.drugstore.data.repository.core.Active
import com.drugstore.data.repository.core.Cancelled
import com.drugstore.domain.entity.Category
import com.kasprov.android.core.lifecycle.BaseViewModel
import com.kasprov.android.core.lifecycle.combine
import com.kasprov.android.core.lifecycle.set
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CategoriesVM @ViewModelInject internal constructor(
    application: Application,
    catalogRepository: CatalogRepository
) : BaseViewModel() {
    private val categoriesResource = catalogRepository.getCategories()
    val categories = categoriesResource.resource.asLiveData()
    private val categoriesFetchState = categoriesResource.fetchState

    private val categoriesRetryItem = ActionItem(
        application.getString(R.string.categories_retry_update_categories_title),
        application.getString(R.string.categories_retry_update_categories_action),
        View.OnClickListener { refresh() }
    )

    val categoriesFooterItems = categories.combine(categoriesFetchState) { categories, fetchState ->
        when {
            fetchState is Cancelled && categories.isEmpty() -> listOf(categoriesRetryItem)
            else -> null
        }
    }
    val isCategoriesRefreshingIndicatorVisible = categoriesFetchState.map { it is Active }

    private var updateCategoriesJob: Job? = null

    internal fun onStart() {
        if (updateCategoriesJob?.isCompleted == false) return
        updateCategoriesJob = viewModelScope.launch {
            try {
                categoriesResource.update()
            } catch (e: Exception) {
                e.handle(navigateTo)
            }
        }
    }

    internal fun refresh() {
        if (updateCategoriesJob?.isCompleted == false) return
        updateCategoriesJob = viewModelScope.launch {
            try {
                categoriesResource.refresh()
            } catch (e: Exception) {
                e.handle(navigateTo)
            }
        }
    }

    fun products(category: Category) = navigateTo.set(CategoriesFragmentDirections.actionCategoriesToProducts(category.title, category.id))
}