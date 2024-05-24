package com.daniel.appcomprayventa.DetalleVendedor

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.daniel.appcomprayventa.Adaptadores.AdaptadorAnuncio
import com.daniel.appcomprayventa.Comentarios
import com.daniel.appcomprayventa.Constantes
import com.daniel.appcomprayventa.Modelo.ModeloAnuncio
import com.daniel.appcomprayventa.R
import com.daniel.appcomprayventa.databinding.ActivityDetalleVendedorBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleVendedor : AppCompatActivity() {

    private lateinit var  binding : ActivityDetalleVendedorBinding

    private  var uidVendedor = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleVendedorBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        uidVendedor = intent.getStringExtra("uidVendedor").toString()
        cargarInfoVendedor()
        cargarAnunciosVendedor()

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.IvComentarios.setOnClickListener {
            val intent = Intent(this, Comentarios::class.java)
            intent.putExtra("uidVendedor", uidVendedor)
            startActivity(intent)


        }
    }

    private  fun cargarAnunciosVendedor(){
        val anuncioArraList : ArrayList<ModeloAnuncio> = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.orderByChild("uid").equalTo(uidVendedor)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    anuncioArraList.clear()
                    for (ds in snapshot.children){
                        try{
                            val modeloAnuncio = ds.getValue(ModeloAnuncio::class.java)
                            anuncioArraList.add(modeloAnuncio!!)
                        }catch (e: Exception){

                        }
                    }
                    val adaptador =  AdaptadorAnuncio(this@DetalleVendedor, anuncioArraList)
                    binding.anunciosRv.adapter = adaptador

                    val contadorAnuncios = "${anuncioArraList.size}"
                    binding.TvNumAnuncios.text = contadorAnuncios
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }
    private fun cargarInfoVendedor(){
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidVendedor)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val imagen = "${snapshot.child("urlImagenPerfil").value}"
                    val tiempo_r = snapshot.child("tiempo").value as Long

                    val  f_fecha = Constantes.obtenerFecha(tiempo_r)
                    binding.TvNombres.text = nombres
                    binding.TvMiembro.text = f_fecha

                    try {
                        Glide.with(this@DetalleVendedor)
                            .load(imagen)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.IvVendedor)
                    }catch (e:Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}