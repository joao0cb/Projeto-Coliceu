package com.example.projetocoliceu.ui.map

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.projetocoliceu.R
import com.example.projetocoliceu.data.api.RetrofitClient
import com.example.projetocoliceu.data.db.AppDatabase
import com.example.projetocoliceu.data.repository.ArtefatoRepository
import com.example.projetocoliceu.data.repository.MapRepository
import com.example.projetocoliceu.viewmodel.MapViewModel
import com.example.projetocoliceu.viewmodel.MapViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapsFragment : Fragment() {

    private val viewModel: MapViewModel by viewModels {
        MapViewModelFactory(
            ArtefatoRepository(
                apiService = RetrofitClient.apiService,
                dao = AppDatabase.getDatabase(requireContext()).artefatoDao(),
                context = requireContext()
            ),
            MapRepository(
                dao = AppDatabase.getDatabase(requireContext()).mapDao()
            )
        )
    }

    private lateinit var adapter: MapsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerMaps)
        val fabAdd = view.findViewById<Button>(R.id.AddMap)

        adapter = MapsAdapter { map ->
            findNavController().navigate(
                MapsFragmentDirections.actionMapsToMapaArqueologico(map.id)
            )
        }

        recycler.adapter = adapter

        viewModel.maps.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        fabAdd.setOnClickListener {
            showCreateMapDialog()
        }

        enableSwipeToDelete(recycler)
    }

    private fun showCreateMapDialog() {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Nome do mapa")
            .setView(editText)
            .setPositiveButton("Criar") { _, _ ->
                val name = editText.text.toString()
                if (name.isNotEmpty()) viewModel.createMap(name)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enableSwipeToDelete(recycler: RecyclerView) {
        val swipe = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                val map = viewModel.maps.value?.get(vh.adapterPosition)
                if (map != null) viewModel.deleteMap(map)
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(recycler)
    }
}
