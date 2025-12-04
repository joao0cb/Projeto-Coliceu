package com.example.projetocoliceu.ui.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projetocoliceu.R
import com.example.projetocoliceu.data.db.MapEntity


class MapsAdapter(
    private val onClick: (MapEntity) -> Unit
) : RecyclerView.Adapter<MapsAdapter.MapViewHolder>() {

    private var data: List<MapEntity> = emptyList()

    fun submitList(list: List<MapEntity>) {
        data = list
        notifyDataSetChanged()
    }

    inner class MapViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.findViewById<TextView>(R.id.txtMapName)
        fun bind(item: MapEntity) {
            name.text = item.name
            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_map, parent, false)
        return MapViewHolder(view)
    }

    override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size
}
