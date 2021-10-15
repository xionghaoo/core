package xh.zero.core.paging

import androidx.annotation.MainThread
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import xh.zero.core.utils.AppExecutors

/**
 * Paging数据仓库模版
 * params: 这里参数是列表的形式，注意要和api的请求参数一一对应
 */
abstract class PagingRepository<R, T>(private val appExecutors: AppExecutors) {

    @MainThread
    fun loadData(params: List<String>) : Listing<R, T> {
        val sourceFactory = createSourceFactory(appExecutors, params)

//        val livePagedList = sourceFactory.toLiveData(
//            pageSize = pageSize(),
//            fetchExecutor = appExecutors.networkIO()
//        )

        // paging配置
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize())
            .setPrefetchDistance(1)
            .setEnablePlaceholders(true)
//            .setInitialLoadSizeHint(initialLoadSizeHint)
//            .setMaxSize(PagedList.Config.MAX_SIZE_UNBOUNDED)
            .build()

        val livePagedList = LivePagedListBuilder(sourceFactory, config)
            .setInitialLoadKey(null)
            .setBoundaryCallback(null)
            .setFetchExecutor(appExecutors.networkIO())
            .build()

        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }
        val initialResponse = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialResponse
        }
        return Listing(
            pagedList = livePagedList,
            networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                it.networkState
            },
            retry = {
                sourceFactory.sourceLiveData.value?.retryAllFailed()
            },
            refresh = {
                sourceFactory.sourceLiveData.value?.invalidate()
            },
            refreshState = refreshState,
            initialResponse = initialResponse
        )
    }

    abstract fun createSourceFactory(appExecutors: AppExecutors, params: List<String>) : PagingDataFactory<R, T>

    abstract fun pageSize() : Int
}