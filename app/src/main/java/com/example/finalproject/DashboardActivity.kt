package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.models.DashboardItem
import com.example.finalproject.util.DashboardParser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    @Inject lateinit var apiService: ApiService

    private lateinit var adapter: DynamicEntityAdapter
    private val entityList = mutableListOf<DashboardItem>()  // typed items for the adapter

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val keypass = intent.getStringExtra("keypass") ?: ""
        findViewById<TextView>(R.id.tvKeypass)?.text =
            getString(R.string.keypass_format, keypass)

        progressBar = findViewById(R.id.progressBar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = DynamicEntityAdapter(entityList) { entity ->
            // Keep DetailsActivity contract: pass JSON string
            val entityJson = Gson().toJson(entity)
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("entity_json", entityJson)
            intent.putExtra("keypass", keypass)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent); finish()
        }

        fetchEntities(keypass)
    }

    private fun fetchEntities(keypass: String) {
        progressBar.visibility = View.VISIBLE

        apiService.getDashboard(keypass).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressBar.visibility = View.GONE
                try {
                    if (!response.isSuccessful) {
                        Log.e("API_RESPONSE_DEBUG", "Non-200: ${response.errorBody()?.string()}")
                        Toast.makeText(
                            this@DashboardActivity,
                            getString(R.string.api_error, response.code()),
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val raw = response.body()?.string().orEmpty()
                    Log.d("API_RESPONSE_DEBUG", raw)

                    // 1) parse to list of maps (flexible)
                    val asMaps = parseFlexibleList(raw)

                    // Debug: see which keys the API actually returns
                    if (asMaps.isNotEmpty()) {
                        Log.d("DASH_KEYS", "First item keys: ${asMaps.first().keys}")
                    }

                    // 2) map maps -> typed items using our smart title/subtitle extraction
                    val items = asMaps.map { m ->
                        val title = DashboardParser.extractTitle(m)
                        val desc = DashboardParser.extractSubtitle(m)
                            ?: m.entries.firstOrNull {
                                it.key != "id" && it.value?.toString()?.isNotBlank() == true
                            }?.value?.toString()

                        DashboardItem(
                            title = title,
                            name = m["name"]?.toString(),
                            description = desc
                        )
                    }

                    entityList.clear()
                    entityList.addAll(items)
                    adapter.notifyDataSetChanged()

                    if (entityList.isEmpty()) {
                        Toast.makeText(
                            this@DashboardActivity,
                            getString(R.string.no_items_found),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (t: Throwable) {
                    Log.e("PARSE_ERROR", "Failed to parse dashboard JSON", t)
                    Toast.makeText(
                        this@DashboardActivity,
                        getString(R.string.network_error, t.localizedMessage ?: "unknown"),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e("NETWORK_ERROR", "Dashboard request failed", t)
                Toast.makeText(
                    this@DashboardActivity,
                    getString(R.string.network_error, t.localizedMessage ?: "unknown"),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /**
     * Flexible parser:
     * - [ {...}, {...} ]
     * - { "entities": [...]} / { "items": [...]} / { "data": [...] } / { "list": [...] }
     * - { ... } -> single-object list
     */
    private fun parseFlexibleList(raw: String): List<Map<String, Any?>> {
        if (raw.isBlank()) return emptyList()

        val je: JsonElement = try {
            JsonParser().parse(raw)
        } catch (_: Throwable) {
            return emptyList()
        }

        val type = object : TypeToken<List<Map<String, Any?>>>() {}.type
        val gson = Gson()

        return when {
            je.isJsonArray -> gson.fromJson(je, type)
            je.isJsonObject -> {
                val obj: JsonObject = je.asJsonObject
                val candidates = listOf("entities", "items", "data", "list")
                val arr: JsonArray? = candidates
                    .asSequence()
                    .mapNotNull { k -> obj.getAsJsonArray(k) }
                    .firstOrNull()

                if (arr != null) {
                    gson.fromJson(arr, type)
                } else {
                    // No array found; wrap object as a single-item list of a flat map
                    listOf(obj.entrySet().associate { it.key to it.value.toString() })
                }
            }
            else -> emptyList()
        }
    }
}
