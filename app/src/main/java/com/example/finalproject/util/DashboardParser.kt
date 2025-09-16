package com.example.finalproject.util

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

object DashboardParser {
    private val gson: Gson = Gson()

    fun parseEntities(json: String?): List<Map<String, Any?>> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val type = object : TypeToken<List<Map<String, Any?>>>() {}.type
            gson.fromJson<List<Map<String, Any?>>>(json, type) ?: emptyList()
        } catch (_: JsonSyntaxException) {
            emptyList()
        }
    }

    fun extractTitle(item: Map<String, Any?>): String {
        val keys = listOf("title", "name", "username", "id")
        for (k in keys) {
            val v = item[k]?.toString()?.trim()
            if (!v.isNullOrEmpty()) return v
        }
        item.values.firstOrNull { it?.toString()?.isNotBlank() == true }?.let { return it.toString() }
        return "(untitled)"
    }

    fun extractSubtitle(item: Map<String, Any?>): String? {
        val keys = listOf("description", "desc", "email", "status")
        for (k in keys) {
            val v = item[k]?.toString()?.trim()
            if (!v.isNullOrEmpty()) return v
        }
        return null
    }
}

