package com.drugstore.data.repository

import com.drugstore.data.BuildConfig
import com.drugstore.data.database.Converters.Companion.toLocalDateTime
import com.drugstore.data.webservice.customer.model.AddressModel
import com.drugstore.data.webservice.customer.model.OrderModel
import com.drugstore.data.webservice.customer.model.OrderProductModel
import com.drugstore.data.webservice.exception.JsonDataException
import com.drugstore.data.webservice.front.model.CatalogAssetModel
import com.drugstore.data.webservice.front.model.CatalogItemModel
import com.drugstore.data.webservice.front.model.CatalogModel
import com.drugstore.data.webservice.front.model.LocaleModel
import com.drugstore.data.webservice.front.response.GetStaticInfoLegalResponse
import com.drugstore.data.webservice.model.CustomerModel
import com.drugstore.domain.entity.*
import com.drugstore.domain.entity.Address.Type.DHL_PACKSTATION
import com.drugstore.domain.entity.Address.Type.REGULAR
import com.drugstore.domain.entity.ContactDetail.Type.*
import com.drugstore.domain.entity.Gender.FEMALE
import com.drugstore.domain.entity.Gender.MALE
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

internal fun Int.mapGender() = when (this) {
    1 -> MALE
    0 -> FEMALE
    else -> throw JsonDataException().map()
}
internal fun Gender.reverseMap() = when (this) {
    MALE -> 1
    FEMALE -> 0
}


private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
internal fun String.mapDate(): LocalDate = LocalDate.parse(this,
    DATE_FORMATTER
)
internal fun LocalDate.reverseMap(): String = this.format(DATE_FORMATTER)


internal fun LocaleModel.map() = Region(
    id,
    title,
    Region.Country(countryId, extractCountryName())
)

private fun LocaleModel.extractCountryName() = title.run {
    substring(indexOf("(") + 1, indexOf(")"))
}


internal fun GetStaticInfoLegalResponse.mapContactDetails() = listOfNotNull(
    emails?.mapNotNull {
        if (it.isNotBlank()) ContactDetail(
            data = it,
            type = EMAIL
        ) else null
    } ?: emptyList(),
    phones?.mapNotNull {
        if (it.isNotBlank()) ContactDetail(
            data = it,
            type = PHONE
        ) else null
    } ?: emptyList(),
    workingHours?.let {
        listOfNotNull(
            if (it.isNotBlank()) ContactDetail(
                data = it,
                type = SCHEDULE
            ) else null
        )
    } ?: emptyList(),
    addresses?.mapNotNull {
        if (it.isNotBlank()) ContactDetail(
            data = it,
            type = ADDRESS
        ) else null
    } ?: emptyList()
).flatten()


fun String.addSchemeIfMissing() =
    if (startsWith("https://") || startsWith("http://")) this else "https://$this"


internal fun CustomerModel.map() = User(
    id,
    uuid,
    email,
    firstName,
    lastName,
    gender.mapGender(),
    birthdayDate.mapDate(),
    localeId
)


internal fun CatalogModel.mapCategory() = Category(
    id,
    title,
    shortDescription,
    mapChildrenIds(),
    assets.findCategoryImageUri()
)

internal fun CatalogModel.mapChildrenIds() = children.run {
    if (isNotEmpty()) joinToString(",") else null
}

private fun CatalogAssetModel.mapImageUri() = BuildConfig.MEDIA_BASE_URL + src

private fun Array<CatalogAssetModel>.findCategoryImageUri() =
    find { it.type == "main_cat" }?.mapImageUri()


internal fun Array<CatalogModel>.mapAndJoinChildrenIds() = mapNotNull {
    it.mapChildrenIds()
}.takeIf { it.isNotEmpty() }?.run { joinToString(",") }


internal fun CatalogModel.mapProduct() = Product(
    id,
    rootId ?: throw JsonDataException().map(),
    title,
    assets.findProductImageUri(),
    assets.filterProductChildrenImageUris()
)

private fun Array<CatalogAssetModel>.findProductImageUri() =
    find { it.type == "product_img" }?.mapImageUri()

private fun Array<CatalogAssetModel>.filterProductChildrenImageUris() =
    filter { it.type == "carousel_img" }.map { it.mapImageUri() }


internal fun CatalogItemModel.mapProductOne(productId: Int) =
    ProductOne(
        this.productId,
        productId,
        productTitle
    )


internal fun CatalogItemModel.mapProductTwo() =
    ProductTwo(
        id,
        productId,
        qty,
        customQtyTypeStr,
        priceStr
    )


internal fun OrderModel.map(userId: Int) = Order(
    id,
    hashId,
    userId,
    status,
    statusTitle,
    price.toString(),
    image,
    createdAt.toLocalDateTime(),
    sentAt?.toLocalDateTime(),
    shippingTrackingId,
    prescriptionLink,
    orderLink
)

internal fun OrderProductModel.map(orderId: Int) =
    OrderProduct(
        orderId = orderId,
        title = title,
        amountString = priceStr,
        quantityString = "$quantity $qtyType"
    )


internal fun AddressModel.map(country: Region.Country) =
    Address(
        id,
        customerId,
        addressType.mapAddressType(),
        title,
        firstName,
        lastName,
        country,
        zip,
        city,
        street,
        houseNumber,
        company,
        packstationAddress,
        packstationNumber,
        phone
    )

private fun String.mapAddressType() = when (this) {
    "regular" -> REGULAR
    "packstation" -> DHL_PACKSTATION
    else -> throw JsonDataException().map()
}
internal fun Address.Type.reverseMap() = when (this) {
    REGULAR -> "regular"
    DHL_PACKSTATION -> "packstation"
}