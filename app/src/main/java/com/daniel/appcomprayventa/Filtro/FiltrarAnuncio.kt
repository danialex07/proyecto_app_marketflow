package com.daniel.appcomprayventa.Filtro

import android.widget.Filter
import com.daniel.appcomprayventa.Adaptadores.AdaptadorAnuncio
import com.daniel.appcomprayventa.Modelo.ModeloAnuncio
import java.util.Locale

class FiltrarAnuncio(
    private val adaptador : AdaptadorAnuncio,
    private val filtroLista : ArrayList<ModeloAnuncio>

) : Filter() {
    override fun performFiltering(filtro: CharSequence?): FilterResults {
        var filtro = filtro
        var resultados = FilterResults()

        if(!filtro.isNullOrEmpty()){
            filtro = filtro.toString().uppercase(Locale.getDefault())
            val filtroModelo = ArrayList<ModeloAnuncio>()
            for(i in filtroLista.indices){
                if (filtroLista[i].marca.uppercase(Locale.getDefault()).contains(filtro)||
                    filtroLista[i].categoria.uppercase(Locale.getDefault()).contains(filtro)||
                    filtroLista[i].condicion.uppercase(Locale.getDefault()).contains(filtro)||
                    filtroLista[i].titulo.uppercase(Locale.getDefault()).contains(filtro)){
                    filtroModelo.add(filtroLista[i])
                }



            }
            resultados.count = filtroModelo.size
            resultados.values = filtroModelo



        }else{
            resultados.count = filtroLista.size
            resultados.values = filtroLista
        }
        return  resultados


    }

    override fun publishResults(filtro: CharSequence?, resultados: FilterResults) {
        adaptador.anuncioArrayList = resultados.values as ArrayList<ModeloAnuncio>
        adaptador.notifyDataSetChanged()

    }

}