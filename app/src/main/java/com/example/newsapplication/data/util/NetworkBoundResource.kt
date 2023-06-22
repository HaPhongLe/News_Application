package com.example.newsapplication.data.util

import com.example.newsapplication.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


inline fun <RemoteType, LocalType, ResultType> networkBoundResource(
    crossinline query: () -> Flow<LocalType> ,
    crossinline fetch: suspend () -> RemoteType,
    crossinline saveFetchResult: suspend (RemoteType) -> Unit,
    crossinline shouldFetch: (LocalType) -> Boolean = {true},
    crossinline convertLocalToResult: (LocalType) -> ResultType
) = channelFlow{

    val data = query().first()
   if(shouldFetch(data)){
       val loading = launch {
           send(Resource.Loading(data = convertLocalToResult(data)))
       }
        try {
            kotlinx.coroutines.delay(2000)
            saveFetchResult(fetch())
            loading.cancel()
            query().collect { send(Resource.Success( data = convertLocalToResult(it)) ) }

        }catch (e: Throwable){
            query().map { send(Resource.Error(data = convertLocalToResult(it), error = e)) }
        }

    } else {
        query().map { send(Resource.Success(data = convertLocalToResult(it)))  }
    }
}