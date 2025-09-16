package com.example.finalproject.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DashboardParserTest {

    @Test
    fun parseEntities_returnsList() {
        val json = """
            [
              {"id": 1, "title": "Alpha"},
              {"name": "Bravo", "desc": "x"},
              {"username": "charlie"}
            ]
        """.trimIndent()

        val list = DashboardParser.parseEntities(json)

        assertThat(list).hasSize(3)
        assertThat(list[0]["title"]).isEqualTo("Alpha")
        assertThat(list[1]["name"]).isEqualTo("Bravo")
        assertThat(list[2]["username"]).isEqualTo("charlie")
    }

    @Test
    fun extractTitle_prefersTitleNameUsernameId_thenFallback() {
        val a = mapOf("title" to "Alpha")
        val b = mapOf("name" to "Bravo")
        val c = mapOf("username" to "charlie")
        val d = mapOf("id" to 42)
        val e = mapOf("foo" to "bar")

        assertThat(DashboardParser.extractTitle(a)).isEqualTo("Alpha")
        assertThat(DashboardParser.extractTitle(b)).isEqualTo("Bravo")
        assertThat(DashboardParser.extractTitle(c)).isEqualTo("charlie")
        assertThat(DashboardParser.extractTitle(d)).isEqualTo("42")
        assertThat(DashboardParser.extractTitle(e)).isEqualTo("bar")
    }

    @Test
    fun extractTitle_handlesEmptyMap() {
        val empty = emptyMap<String, Any?>()
        assertThat(DashboardParser.extractTitle(empty)).isEqualTo("(untitled)")
    }
}

