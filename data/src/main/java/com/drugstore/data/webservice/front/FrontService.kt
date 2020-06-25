package com.drugstore.data.webservice.front

import com.drugstore.data.webservice.NO_LOCALE_HEADER
import com.drugstore.data.webservice.front.model.CatalogModel
import com.drugstore.data.webservice.front.model.LocaleModel
import com.drugstore.data.webservice.front.request.PostContactUsRequest
import com.drugstore.data.webservice.front.request.PostPasswordRequest
import com.drugstore.data.webservice.front.response.GetStaticInfoCommonResponse
import com.drugstore.data.webservice.front.response.GetStaticInfoLegalResponse
import com.drugstore.data.webservice.front.response.GetStaticInfoLinksResponse
import com.drugstore.data.webservice.response.SuccessResponse
import retrofit2.http.*

interface FrontService {

    @GET("front/locale/index")
    @Headers(NO_LOCALE_HEADER)
    suspend fun getLocaleIndex(): List<LocaleModel>

    @GET("front/catalog/index?$CATALOG_TYPE_ROOT&$COUNTRY_ID_TYPE_BY_LOCALE")
    suspend fun getCatalogIndexRoot(): Array<CatalogModel>

    @GET("front/catalog/index?$CATALOG_TYPE_CATEGORY_1&$CATALOG_IDS_TYPE_IN&$COUNTRY_ID_TYPE_BY_LOCALE")
    suspend fun getCatalogIndexCategory1(@Query(QUERY_PARAM_NAME_CATALOG_IDS_VALUE) catalog_ids_value: String): Array<CatalogModel>

    @GET("front/catalog/index?$CATALOG_TYPE_PRODUCT&$CATALOG_IDS_TYPE_IN&$COUNTRY_ID_TYPE_BY_LOCALE")
    suspend fun getCatalogIndexProduct(@Query(QUERY_PARAM_NAME_CATALOG_IDS_VALUE) catalog_ids_value: String): Array<CatalogModel>

    @POST("front/contact-us")
    suspend fun postContactUs(@Body request: PostContactUsRequest): SuccessResponse

    @GET("front/static-info/legal")
    suspend fun getStaticInfoLegal(): GetStaticInfoLegalResponse

    @GET("front/static-info/links")
    suspend fun getStaticInfoLinks(): GetStaticInfoLinksResponse

    @GET("front/static-info/common")
    suspend fun getStaticInfoCommon(): GetStaticInfoCommonResponse

    @POST("front/password")
    suspend fun postPassword(@Body request: PostPasswordRequest): SuccessResponse

    companion object {

        private const val QUERY_PARAM_NAME_CATALOG_TYPE_TYPE = "catalog_type[type]"
        private const val CATALOG_TYPE_ROOT = "$QUERY_PARAM_NAME_CATALOG_TYPE_TYPE=root"
        private const val CATALOG_TYPE_CATEGORY_1 = "$QUERY_PARAM_NAME_CATALOG_TYPE_TYPE=category_1"
        private const val CATALOG_TYPE_PRODUCT = "$QUERY_PARAM_NAME_CATALOG_TYPE_TYPE=product"

        private const val QUERY_PARAM_NAME_CATALOG_IDS_TYPE = "catalog_ids[type]"
        private const val CATALOG_IDS_TYPE_IN = "$QUERY_PARAM_NAME_CATALOG_IDS_TYPE=in"

        private const val QUERY_PARAM_NAME_CATALOG_IDS_VALUE = "catalog_ids[value]"

        private const val QUERY_PARAM_NAME_COUNTRY_ID_TYPE = "country_id[type]"
        private const val COUNTRY_ID_TYPE_BY_LOCALE = "$QUERY_PARAM_NAME_COUNTRY_ID_TYPE=by_locale"
    }
}