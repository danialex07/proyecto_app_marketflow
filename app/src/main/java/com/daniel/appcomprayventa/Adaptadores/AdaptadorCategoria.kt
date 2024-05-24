package com.daniel.appcomprayventa.Adaptadores

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.daniel.appcomprayventa.Modelo.ModeloCategoria
import com.daniel.appcomprayventa.RvListenerCategoria
import com.daniel.appcomprayventa.databinding.ItemCategoriaInicioBinding
import kotlin.random.Random

class AdaptadorCategoria (
    private val context : Context,
    private val categoria_ArrayList : ArrayList<ModeloCategoria>,
    private val rvListenerCategoria: RvListenerCategoria
    ):Adapter<AdaptadorCategoria.HolderCategoria>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria {
        binding = ItemCategoriaInicioBinding.inflate(LayoutInflater.from(context),parent, false)
        return HolderCategoria(binding.root)
    }

    override fun getItemCount(): Int {
        return categoria_ArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoria, position: Int) {
        val modeloCategoria = categoria_ArrayList[position]

        val icono = modeloCategoria.icon
        val categoria  = modeloCategoria.categoria
        val random = java.util.Random()
        val color= Color.argb(
            255,
            random.nextInt(255),
            random.nextInt(255),
            random.nextInt(255)
        )
        holder.categoriaIconoIv.setImageResource(icono)
        holder.categoriaTv.text = categoria
        holder.categoriaIconoIv.setBackgroundColor(color)

        holder.itemView.setOnClickListener {
            rvListenerCategoria.onCategoriaClick(modeloCategoria)

        }
    }

    private lateinit var binding: ItemCategoriaInicioBinding
    inner class HolderCategoria(itemView : View):ViewHolder(itemView){
        var categoriaIconoIv = binding.categoriaIconoIv
        var categoriaTv = binding.TvCategoria
    }



}