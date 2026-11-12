package com.amarchaud.data.fakes

import com.amarchaud.data.api.PaginationDemoApi
import com.amarchaud.data.models.ResultsDataModel

class FakePaginationDemoApi(
    var result: Result<ResultsDataModel> = Result.success(ResultsDataModel()),
) : PaginationDemoApi {

    val requestedPages = mutableListOf<Int>()

    override suspend fun getRandomUsers(seed: String, results: Int, page: Int): Result<ResultsDataModel> {
        requestedPages += page
        return result
    }
}
