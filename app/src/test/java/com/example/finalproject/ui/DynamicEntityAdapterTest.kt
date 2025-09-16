package com.example.finalproject.ui

import android.widget.FrameLayout
import android.widget.TextView
import com.example.finalproject.R
import com.example.finalproject.DynamicEntityAdapter // <- adjust if your adapter is in another package
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.robolectric.Robolectric

class DynamicEntityAdapterTest {

    @Test
    fun bind_setsTitle_and_invokesClick() {
        // Simple parent for creating the ViewHolder
        val activity = Robolectric.buildActivity(android.app.Activity::class.java).setup().get()
        val parent = FrameLayout(activity)

        // Track the click result
        var clickedTitle: String? = null

        // Minimal row item your adapter can bind
        val item: Map<String, Any?> = mapOf(
            "title" to "Test Item",
            "description" to "Lorem ipsum"
        )

        // ✅ Your adapter expects (data: List<Map<...>>, onClick: (Map<...>) -> Unit)
        val adapter = DynamicEntityAdapter(
            data = listOf(item) as List<Map<String, Any>>,
            onClick = { it: Map<String, Any?> ->
                clickedTitle = it["title"]?.toString()
            }
        )

        // Create/bind the first row
        val vh = adapter.onCreateViewHolder(parent, /* viewType = */ 0)
        adapter.onBindViewHolder(vh, /* position = */ 0)

        // Assert the title text is shown
        val titleView = vh.itemView.findViewById<TextView>(R.id.tvTitle)
        assertThat(titleView).isNotNull()
        assertThat(titleView.text.toString()).isEqualTo("Test Item")

        // Simulate a click on the row and assert the callback fired
        vh.itemView.performClick()
        assertThat(clickedTitle).isEqualTo("Test Item")
    }
}
