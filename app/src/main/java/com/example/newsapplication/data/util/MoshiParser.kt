package com.example.newsapplication.data.util

import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class MoshiParser(
    val moshi: Moshi,
): JsonParser {
    override fun <T> toJson(obj: T, type: Type): String? {
        val parser = moshi.adapter<T>(type)
        return parser.toJson(obj)
    }

    override fun <T> fromJson(json: String, type: Type): T? {
        val parser = moshi.adapter<T>(type)
        return parser.fromJson(json)
    }


}