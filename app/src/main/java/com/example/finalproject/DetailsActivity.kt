package com.example.finalproject

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Top bar actions
        findViewById<View>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        findViewById<View>(R.id.btnLogout).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Get the entity payload passed from Dashboard
        val entityJson = intent.getStringExtra("entity_json")
        if (entityJson.isNullOrBlank()) {
            Toast.makeText(this, "No entity data found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Parse into a Map<String, Any>
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val entity: Map<String, Any> = try {
            Gson().fromJson<Map<String, Any>>(entityJson, type) ?: emptyMap()
        } catch (t: Throwable) {
            Toast.makeText(this, "Invalid entity data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Render as read-only rows
        val container = findViewById<LinearLayout>(R.id.detailsContainer)
        renderEntity(container, entity)
    }

    private fun renderEntity(container: LinearLayout, entity: Map<String, Any>) {
        container.removeAllViews()

        // Optional: show important keys first in a nice order
        val preferredOrder = listOf("destination", "country", "bestSeason", "popularAttraction", "description")

        // Add preferred keys first (if present)
        for (key in preferredOrder) {
            entity[key]?.let { value ->
                addRow(container, prettifyKey(key), value.toString())
            }
        }

        // Add any remaining keys not in preferred list
        entity.keys
            .filter { it !in preferredOrder }
            .sorted()
            .forEach { key ->
                val value = entity[key]
                addRow(container, prettifyKey(key), value?.toString() ?: "")
            }
    }

    private fun addRow(container: LinearLayout, label: String, value: String) {
        // Label (bold, smaller)
        val tvLabel = TextView(this).apply {
            text = label
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
        }
        container.addView(tvLabel)

        // Value (regular, larger; allow multi-line)
        val tvValue = TextView(this).apply {
            text = value
            textSize = 16f
        }
        container.addView(tvValue)

        // Spacer
        val spacer = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(12)
            )
        }
        container.addView(spacer)
    }

    private fun prettifyKey(raw: String): String {
        // Turn "bestSeason" or "popular_attraction" into "Best Season", "Popular Attraction"
        return raw
            .replace('_', ' ')
            .split(Regex("(?=[A-Z])|\\s+"))
            .filter { it.isNotBlank() }
            .joinToString(" ") { part ->
                part.lowercase().replaceFirstChar { it.titlecase() }
            }
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}
