package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.models.DashboardItem

class DynamicEntityAdapter(
    private var data: List<DashboardItem>,
    private val onClick: (DashboardItem) -> Unit
) : RecyclerView.Adapter<DynamicEntityAdapter.ViewHolder>() {

    fun updateData(newData: List<DashboardItem>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entity, parent, false) // keep your existing row layout name
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        // Prefer title -> name -> fallback
        val title = item.title?.takeIf { it.isNotBlank() }
            ?: item.name?.takeIf { it.isNotBlank() }
            ?: "(untitled)"
        holder.title.text = title
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle) // ensure this id exists in your row XML
    }
}
