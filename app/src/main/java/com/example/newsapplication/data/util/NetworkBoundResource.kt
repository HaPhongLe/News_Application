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
            val remoteData = fetch()
            saveFetchResult(remoteData)
            loading.cancel()
            query().collect { send(Resource.Success( data = convertLocalToResult(it)) ) }

        }catch (e: Throwable){
            query().collect{ send(Resource.Error(data = convertLocalToResult(it), error = e)) }
        }

    } else {
        query().collect { send(Resource.Success(data = convertLocalToResult(it)))  }
    }
}