package com.daniel.appcomprayventa.Adaptadores

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
//import android.widget.Adapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.daniel.appcomprayventa.Constantes
import com.daniel.appcomprayventa.DetalleAnuncio.DetalleAnuncio
import com.daniel.appcomprayventa.Filtro.FiltrarAnuncio
import com.daniel.appcomprayventa.Modelo.ModeloAnuncio
import com.daniel.appcomprayventa.R
import com.daniel.appcomprayventa.databinding.ItemAnuncioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorAnuncio :RecyclerView. Adapter<AdaptadorAnuncio.HolderAnuncio>, Filterable {


    private lateinit var binding: ItemAnuncioBinding


    private var context : Context
    var anuncioArrayList : ArrayList<ModeloAnuncio>
    private var firebaseAuth : FirebaseAuth
    private var filtroLista : ArrayList<ModeloAnuncio>
    private var filtro : FiltrarAnuncio ?= null

    constructor(context: Context, anuncioArrayList: ArrayList<ModeloAnuncio>) {
        this.context = context
        this.anuncioArrayList = anuncioArrayList
        firebaseAuth = FirebaseAuth.getInstance()
        this.filtroLista = anuncioArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAnuncio {
        binding =  ItemAnuncioBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderAnuncio(binding.root)

    }

    override fun getItemCount(): Int {
        return anuncioArrayList.size
    }

    override fun onBindViewHolder(holder: HolderAnuncio, position: Int) {
        val modeloAnuncio = anuncioArrayList[position]

        val titulo = modeloAnuncio.titulo
        val descripcion = modeloAnuncio.descripcion
        val direccion = modeloAnuncio.direccion
        val condicion =  modeloAnuncio.condicion
        val precio = modeloAnuncio.precio
        val tiempo =modeloAnuncio.tiempo

        val formatoFecha = Constantes.obtenerFecha(tiempo)

        cargarPrimeraImgAnuncio(modeloAnuncio,holder)
        comprobarFavorito(modeloAnuncio,holder)

        holder.Tv_titulo.text=titulo
        holder.Tv_descripcion.text= descripcion
        holder.Tv_direccion.text= direccion
        holder.Tv_condicion.text = condicion
        holder.Tv_precio.text = precio
        holder.Tv_fecha.text=formatoFecha

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetalleAnuncio::class.java)
            intent.putExtra("idAnuncio",modeloAnuncio.id)
            context.startActivity(intent)

        }

        if(condicion.equals("Nuevo")){
            holder.Tv_condicion.setTextColor(Color.parseColor("#48C9B0"))
        }else if(condicion.equals("usado")){
            holder.Tv_condicion.setTextColor(Color.parseColor("#5DADE2"))
        }else if(condicion.equals("Renovado")){
            holder.Tv_condicion.setTextColor(Color.parseColor("#A569BD"))
        }


        holder.Ib_fav.setOnClickListener {
            val favorito = modeloAnuncio.favorito
            if (favorito){
                //Favoritos = true
                Constantes.eliminarAnuncioFav(context, modeloAnuncio.id)
            }else{
                //favoritos = false
                Constantes.agregarAnuncioFav(context,modeloAnuncio.id)
            }
        }


    }

    private fun comprobarFavorito(modeloAnuncio: ModeloAnuncio, holder: AdaptadorAnuncio.HolderAnuncio) {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(modeloAnuncio.id)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorito = snapshot.exists()
                    modeloAnuncio.favorito = favorito
                    if (favorito){
                        holder.Ib_fav.setImageResource(R.drawable.ic_anuncio_es_favorito)
                    }else{
                        holder.Ib_fav.setImageResource(R.drawable.ic_no_favorito)
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }

    private fun cargarPrimeraImgAnuncio(modeloAnuncio: ModeloAnuncio, holder: AdaptadorAnuncio.HolderAnuncio) {
        val idAnuncio = modeloAnuncio.id

        val ref=FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(idAnuncio).child("Imagenes").limitToFirst(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val imagenUrl = "${ds.child("imagenUrl").value}"
                        try {
                            Glide.with(context)
                                .load(imagenUrl)
                                .placeholder(R.drawable.ic_imagen)
                                .into(holder.imagenIv)

                        }catch (e:Exception){

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }


    inner  class  HolderAnuncio(itemView : View) : RecyclerView.ViewHolder(itemView){
        var imagenIv = binding.imagenIv
        var Tv_titulo = binding.TvTitulo
        var Tv_descripcion = binding.TvDescripcion
        var Tv_direccion = binding.TvDireccion
        var Tv_condicion = binding.TvCondicion
        var Tv_precio = binding.TvPrecio
        var Tv_fecha = binding.TvFecha
        var Ib_fav = binding.IbFav
    }

    override fun getFilter(): Filter {
        if (filtro == null){
            filtro = FiltrarAnuncio(this,filtroLista)
        }
        return  filtro as FiltrarAnuncio
    }


}