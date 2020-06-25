package com.drugstore.data.webservice.front

import com.drugstore.data.webservice.ErrorHandler
import com.drugstore.data.webservice.front.request.PostContactUsRequest
import com.drugstore.data.webservice.front.request.PostPasswordRequest

internal class FrontServiceProxy(
    private val errorHandler: ErrorHandler,
    private val frontService: FrontService
) : FrontService {

    override suspend fun getLocaleIndex() =
        errorHandler { frontService.getLocaleIndex() }

    override suspend fun getCatalogIndexRoot() =
        errorHandler { frontService.getCatalogIndexRoot() }

    override suspend fun getCatalogIndexCategory1(catalog_ids_value: String) =
        errorHandler { frontService.getCatalogIndexCategory1(catalog_ids_value) }

    override suspend fun getCatalogIndexProduct(catalog_ids_value: String) =
        errorHandler { frontService.getCatalogIndexProduct(catalog_ids_value) }

    override suspend fun postContactUs(request: PostContactUsRequest) =
        errorHandler { frontService.postContactUs(request) }

    override suspend fun getStaticInfoLegal() =
        errorHandler { frontService.getStaticInfoLegal() }

    override suspend fun getStaticInfoLinks() =
        errorHandler { frontService.getStaticInfoLinks() }

    override suspend fun getStaticInfoCommon() =
        errorHandler { frontService.getStaticInfoCommon() }

    override suspend fun postPassword(request: PostPasswordRequest) =
        errorHandler { frontService.postPassword(request) }
}