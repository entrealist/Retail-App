package com.drugstore.app.login.login.register

import android.content.Context
import android.os.Bundle
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drugstore.app.core.dialog.PickDateDialogFragment
import com.drugstore.app.core.dialog.PickItemDialogFragment
import com.drugstore.app.core.findFirstContainedKey
import com.drugstore.app.core.genderName
import com.drugstore.app.core.handle
import com.drugstore.app.core.linkifyHtml
import com.drugstore.data.repository.AuthRepository
import com.drugstore.data.repository.CommonRepository
import com.drugstore.data.repository.core.Active
import com.drugstore.data.repository.core.Cancelled
import com.drugstore.data.repository.entity.Input
import com.drugstore.domain.entity.Gender
import com.kasprov.android.core.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class RegisterVM @ViewModelInject internal constructor(
    @Assisted private val handle: SavedStateHandle,
    private val authRepository: AuthRepository,
    commonRepository: CommonRepository
) : BaseViewModel() {
    private val userConsentResource = commonRepository.getRegistrationUserConsent()
    val userConsent = userConsentResource.resource.map {
        withContext(Dispatchers.Default) {
            it?.linkifyHtml()
        }
    }.asLiveData()
    private val userConsentFetchState = userConsentResource.fetchState

    val email = MutableLiveData<String?>()
    val firstName = MutableLiveData<String?>()
    val lastName = MutableLiveData<String?>()
    val password = MutableLiveData<String?>()
    val repeatedPassword = MutableLiveData<String?>()

    private val _gender = handle.getLiveData<Gender?>("gender", Gender.MALE)
    val gender: LiveData<Gender?> = _gender

    private val _birthdayDate = handle.getLiveData<LocalDate?>("birthdayDate")
    val birthdayDate: LiveData<LocalDate?> = _birthdayDate

    private val _inputErrors = handle.getLiveData<MutableMap<Input, String>?>("input_errors")
    val inputErrors: LiveData<out Map<Input, String>?> = _inputErrors
    var inputs: Set<Input> = emptySet()

    val isRetryUpdateUserConsentControlVisible = userConsent.combineNullable(userConsentFetchState) { userConsent, fetchState ->
        fetchState is Cancelled && userConsent.isNullOrBlank()
    }
    val isUserConsentLoadingIndicatorVisible = userConsent.combineNullable(userConsentFetchState) { userConsent, fetchState ->
        fetchState is Active && userConsent.isNullOrBlank()
    }

    private val focusInput = MutableLiveData<Event<Input>?>()
    val onFocusInput: LiveData<Event<Input>?> = focusInput

    private val _isLoading = MutableLiveData<Boolean?>(false)
    val isLoadingIndicatorVisible: LiveData<Boolean?> = _isLoading
    val areControlsEnabled: LiveData<Boolean?> = _isLoading.mapNonNull { !it }

    init {
        updateUserConsent()
    }

    fun updateUserConsent() = viewModelScope.launch {
        try {
            userConsentResource.update()
        } catch (e: Exception) {
            e.handle(navigateTo)
        }
    }

    fun pickGender(context: Context) {
        val checkedItemId = (_gender.value ?: Gender.MALE).id
        val labels = Array(Gender.values.size) { i -> context.genderName(
            Gender.values[i]) ?: "" }
        navigateTo.set(
            RegisterFragmentDirections.actionToPickItem(
                Gender.ids,
                checkedItemId,
                labels,
                FragmentRequest.PICK_GENDER.key
            )
        )
    }

    fun pickBirthdayDate() {
        val initialDate = _birthdayDate.value ?: LocalDate.now()
        navigateTo.set(
            RegisterFragmentDirections.actionToPickDate(
                FragmentRequest.PICK_BIRTHDAY.key,
                initialDate
            )
        )
    }

    internal fun onFragmentResult(request: FragmentRequest, result: Bundle) = when (request) {
        FragmentRequest.PICK_GENDER -> {
            val pickedItemId = PickItemDialogFragment.unpackPickedItemId(result)
            _gender.value = Gender.getById(pickedItemId)
        }
        FragmentRequest.PICK_BIRTHDAY -> {
            _birthdayDate.value = PickDateDialogFragment.unpackPickedDate(result)
        }
    }

    fun submit() {
        inputErrors.value?.findFirstContainedKey(inputs)?.let {
            focusInput.set(it)
            return
        }

        val email = email.value
        val firstName = firstName.value
        val lastName = lastName.value
        val password = password.value
        val repeatedPassword = repeatedPassword.value
        val gender = gender.value
        val birthdayDate = birthdayDate.value

        viewModelScope.launch {
            try {
                _isLoading.value = true
                authRepository.register(email, firstName, lastName, password, repeatedPassword, gender, birthdayDate)
            } catch (e: Exception) {
                _isLoading.value = false
                e.handle(navigateTo) {
                    _inputErrors.value = errors?.toMutableMap()
                    errors?.findFirstContainedKey(inputs)?.let { focusInput.set(it) }
                }
            }
        }
    }

    fun clearInputError(input: Input) = _inputErrors.value?.takeIf { it.containsKey(input) }?.let {
        _inputErrors.value = it.apply { remove(input) }
    }

    enum class FragmentRequest(val key: String) {
        PICK_GENDER("register_pick_gender"),
        PICK_BIRTHDAY("register_pick_birthday")
    }
}