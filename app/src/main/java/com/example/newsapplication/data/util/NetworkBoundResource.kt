package com.example.newsapplication.data.util

import com.example.newsapplication.util.Resource
import kotlinx.coroutines.flow.*


inline fun <RemoteType, LocalType, ResultType> networkBoundResource(
    crossinline query: () -> Flow<LocalType> ,
    crossinline fetch: suspend () -> RemoteType,
    crossinline saveFetchResult: suspend (RemoteType) -> Unit,
    crossinline shouldFetch: (LocalType) -> Boolean = {true},
    crossinline convertLocalToResult: (LocalType) -> ResultType
) = flow{

    val data = query().first()
    val flow = if(shouldFetch(data)){
        emit(Resource.Loading(data = convertLocalToResult(data)))
        try {
            saveFetchResult(fetch())
            query().map { Resource.Success( data = convertLocalToResult(it)) }

        }catch (e: Throwable){
            query().map {  Resource.Error(data = convertLocalToResult(it), error = e)}
        }

    } else {
        query().map { Resource.Success(data = convertLocalToResult(data)) }
    }
    emitAll(flow)
}