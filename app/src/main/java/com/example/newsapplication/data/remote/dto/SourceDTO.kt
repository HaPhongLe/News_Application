package com.example.newsapplication.data.remote.dto

import com.example.newsapplication.domain.model.Source

data class SourceDTO (
    val id: String?,
    val name: String
) {
    fun toSource(): Source{
        return Source(
            id = id,
            name = name
        )
    }
}