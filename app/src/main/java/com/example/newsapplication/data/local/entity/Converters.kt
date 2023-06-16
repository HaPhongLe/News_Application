package com.example.newsapplication.data.local.entity

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.newsapplication.data.util.JsonParser
import com.example.newsapplication.domain.model.Source
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.Types

@ProvidedTypeConverter
class Converters (
    private val jsonParser: JsonParser
) {
    val type = Types.getRawType(Source::class.java)
    @TypeConverter
    fun fromSourceJson(json: String): Source{
        return jsonParser.fromJson<Source>(
            json = json,
//            type = Types.newParameterizedType(Source::class.java)
//            type = object: TypeToken<Source>(){}.type
        type = type
        )?: Source(null, "")
    }

    @TypeConverter
    fun toSourceJson(source: Source): String{
        return jsonParser.toJson(
            obj = source,
//            type = Types.newParameterizedType(Source::class.java)
//            type = object: TypeToken<Source>(){}.type
        type = type
        )?: ""
    }

}