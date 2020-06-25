package com.drugstore.app.main.categories.products.product

import android.os.Bundle
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drugstore.CoreGraphDirections
import com.drugstore.app.core.dialog.PickItemDialogFragment
import com.drugstore.app.core.handle
import com.drugstore.data.repository.CatalogRepository
import com.drugstore.data.repository.CommonRepository
import com.drugstore.domain.entity.ProductOneWithChildren
import com.drugstore.domain.entity.ProductTwo
import com.kasprov.android.core.lifecycle.*
import kotlinx.coroutines.launch

class ProductVM @ViewModelInject internal constructor(
    @Assisted handle: SavedStateHandle,
    catalogRepository: CatalogRepository,
    private val commonRepository: CommonRepository
) : BaseViewModel() {
    private val productId: Int = handle["productId"] ?: throw IllegalArgumentException("No productId provided")
    val product = catalogRepository.getProductWithChildren(productId).asLiveData()

    private val _productOne = MediatorLiveData<ProductOneWithChildren>().apply {
        addNonNullSource(this) { handle["productOneId"] = it.id }
        addOneTimeNonNullSource(product) { product ->
            value = handle.get<Int?>("productOneId")?.let { id ->
                product.children.find { it.id == id }
            } ?: product.children.firstOrNull()
        }
    }
    val productOne: LiveData<ProductOneWithChildren> = _productOne

    private val _productTwo = MediatorLiveData<ProductTwo>().apply {
        addNonNullSource(this) { handle["productTwoIdFor${it.productOneId}"] = it.id }
        addNonNullSource(productOne) { productOne ->
            value = handle.get<Int?>("productTwoIdFor${productOne.id}")?.let { id ->
                productOne.children.find { it.id == id }
            } ?: productOne.children.minBy { it.quantity }
        }
    }
    val productTwo: LiveData<ProductTwo> = _productTwo

    private val _isImageEnlarged = handle.getLiveData<Boolean?>("isImageEnlarged", false)
    val isImageEnlarged: LiveData<Boolean?> = _isImageEnlarged

    private val _isLoading = MutableLiveData<Boolean?>(false)
    val isLoadingIndicatorVisible: LiveData<Boolean?> = _isLoading
    val areControlsEnabled: LiveData<Boolean?> = _isLoading.mapNonNull { !it }

    fun pickProductOne() {
        val productOnes = product.value?.children ?: return
        val ids = productOnes.map { it.id }.toIntArray()
        val checkedItemId = productOne.value?.id ?: return
        val labels = productOnes.map { it.title }.toTypedArray()
        navigateTo.set(
            ProductFragmentDirections.actionToPickItem(
                ids,
                checkedItemId,
                labels,
                FragmentRequest.PICK_PRODUCT_ONE.key
            )
        )
    }

    fun pickProductTwo() {
        val productTwos = productOne.value?.children?.sortedBy { it.quantity } ?: return
        val ids = productTwos.map { it.id }.toIntArray()
        val checkedItemId = productTwo.value?.id ?: return
        val labels = productTwos.mapNotNull { it.label() }.toTypedArray()
        navigateTo.set(
            ProductFragmentDirections.actionToPickItem(
                ids,
                checkedItemId,
                labels,
                FragmentRequest.PICK_PRODUCT_TWO.key
            )
        )
    }

    internal fun onFragmentResult(request: FragmentRequest, result: Bundle) = when (request) {
        FragmentRequest.PICK_PRODUCT_ONE -> {
            val pickedItemId = PickItemDialogFragment.unpackPickedItemId(result)
            _productOne.value = product.value?.children?.find { it.id == pickedItemId }
        }
        FragmentRequest.PICK_PRODUCT_TWO -> {
            val pickedItemId = PickItemDialogFragment.unpackPickedItemId(result)
            _productTwo.value = productOne.value?.children?.find { it.id == pickedItemId }
        }
    }

    fun toggleImage() {
        _isImageEnlarged.value = _isImageEnlarged.value?.not()
    }

    fun onBackPressed() {
        if (_isImageEnlarged.value == true) _isImageEnlarged.value = false else navigateUp.call()
    }

    fun order() {
        val productOneId = productOne.value?.id ?: return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                commonRepository.getOrderRequest(productId, productOneId)?.let {
                    navigateTo.set(CoreGraphDirections.actionToWebView(it))
                }
            } catch (e: Exception) {
                e.handle(navigateTo)
            } finally {
                _isLoading.value = false
            }
        }
    }

    enum class FragmentRequest(val key: String) {
        PICK_PRODUCT_ONE("product_pick_product_one"),
        PICK_PRODUCT_TWO("product_pick_product_two")
    }
}