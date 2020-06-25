package com.drugstore.data.repository

import androidx.lifecycle.MutableLiveData
import com.drugstore.data.database.dao.CategoriesDao
import com.drugstore.data.database.dao.ProductsDao
import com.drugstore.data.repository.core.*
import com.drugstore.data.webservice.front.FrontService
import com.drugstore.domain.entity.Category
import com.drugstore.domain.entity.Product
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import org.threeten.bp.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepository @Inject constructor(
    private val cacheRecordsRepository: CacheRecordsRepository,
    private val categoriesDao: CategoriesDao,
    private val productsDao: ProductsDao,
    private val frontService: FrontService
) {

    fun getCategories(): Resource<List<Category>> {
        val fetchState = MutableLiveData<JobState?>(
            Completed
        )

        val refresh = suspend {
            try {
                fetchState.value = Active
                refreshCategories()
                fetchState.value = Completed
            } catch (e: Exception) {
                fetchState.value =
                    Cancelled(e)
                throw e
            }
        }

        val update = suspend {
            if (cacheRecordsRepository.isCacheExpired(KEY_CATEGORIES)) refresh()
        }

        return Resource(
            categoriesDao.get(),
            fetchState,
            refresh,
            update
        )
    }

    internal suspend fun refreshCategories() = withContext(Default) {
        val response =
            call { frontService.getCatalogIndexRoot() }
        val categories = response.map { it.mapCategory() }
        categoriesDao.deleteAndInsert(categories)
        cacheRecordsRepository.setCacheExpiration(
            KEY_CATEGORIES,
            LIFESPAN_CATEGORIES
        )
        cacheRecordsRepository.expireCachesThatStartWith(KEY_PRODUCTS_PREFIX)
    }

    fun getProductsByCategory(categoryId: Int): Resource<List<Product>> {
        val fetchState = MutableLiveData<JobState?>(
            Completed
        )

        val refresh = suspend {
            try {
                fetchState.value = Active
                refreshProducts(categoryId)
                fetchState.value = Completed
            } catch (e: Exception) {
                fetchState.value =
                    Cancelled(e)
                throw e
            }
        }

        val update = suspend {
            if (cacheRecordsRepository.isCacheExpired(
                    keyProducts(
                        categoryId
                    )
                )) refresh()
        }

        return Resource(
            productsDao.getByCategory(
                categoryId
            ), fetchState, refresh, update
        )
    }

    internal suspend fun refreshProducts(categoryId: Int) = withContext(Default) {
        val categoryOnesIds = categoriesDao.getChildrenIdsSync(categoryId) ?: return@withContext
        val categoryOnesResponse = call {
            frontService.getCatalogIndexCategory1(categoryOnesIds)
        }

        val productsIds = categoryOnesResponse.mapAndJoinChildrenIds() ?: return@withContext
        val productsResponse = call {
            frontService.getCatalogIndexProduct(productsIds)
        }

        val products = productsResponse.map { it.mapProduct() }

        val productOnes = productsResponse
            .map { product ->
                product.items
                    .distinctBy { it.productId }
                    .map { it.mapProductOne(product.id) }
            }
            .flatten()

        val productTwos = productsResponse
            .map { product ->
                product.items
                    .groupBy { it.productId }
                    .values
                    .flatten()
                    .map { it.mapProductTwo() }
            }
            .flatten()

        productsDao.deleteAndInsert(categoryId, products, productOnes, productTwos)
        cacheRecordsRepository.setCacheExpiration(
            keyProducts(
                categoryId
            ),
            LIFESPAN_PRODUCTS
        )
    }

    fun getProductWithChildren(id: Int) = productsDao.getWithChildren(id)

    companion object {
        private const val KEY_CATEGORIES = "categories"
        private val LIFESPAN_CATEGORIES: Duration = Duration.ofHours(1)

        private const val KEY_PRODUCTS_PREFIX = "products_of_category_"
        private fun keyProducts(categoryId: Int) = "$KEY_PRODUCTS_PREFIX$categoryId"
        private val LIFESPAN_PRODUCTS: Duration = Duration.ofHours(1)
    }
}