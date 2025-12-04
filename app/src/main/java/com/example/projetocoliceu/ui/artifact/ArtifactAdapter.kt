package com.example.projetocoliceu.ui.artifact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projetocoliceu.databinding.ItemArtifactBinding
import com.example.projetocoliceu.data.model.Artefato

class ArtifactAdapter(
    private val onClick: (Artefato) -> Unit
) : RecyclerView.Adapter<ArtifactAdapter.ViewHolder>() {

    private var list: List<Artefato> = emptyList()

    fun submitList(newList: List<Artefato>) {
        list = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemArtifactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Artefato) {
            binding.txtName.text = item.nome
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemArtifactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
