package com.daniel.appcomprayventa.Fragmentos

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.daniel.appcomprayventa.Adaptadores.AdaptadorAnuncio
import com.daniel.appcomprayventa.Modelo.ModeloAnuncio
import com.daniel.appcomprayventa.R
import com.daniel.appcomprayventa.databinding.FragmentMisAnunciosBinding
import com.daniel.appcomprayventa.databinding.FragmentMisAnunciosPublicadosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Mis_Anuncios_Publicados_Fragment : Fragment() {

    private lateinit var binding: FragmentMisAnunciosPublicadosBinding
    private lateinit var mContext : Context
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var anunciosArrayList: ArrayList<ModeloAnuncio>
    private lateinit var anunciosAdaptador: AdaptadorAnuncio


    override fun onAttach(context: Context) {
        this.mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentMisAnunciosPublicadosBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        cargarMisAnuncios()

        binding.EtBuscar.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(filtro: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    val consulta = filtro.toString()
                    anunciosAdaptador.filter.filter(consulta)

                }catch (e:Exception){

                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.IbLimpiar.setOnClickListener{
            val consulta = binding.EtBuscar.text.toString().trim()
            if (consulta.isNotEmpty()){
                binding.EtBuscar.setText("")
                Toast.makeText(context,
                    "se ha limpiado el campo de busqueda",
                    Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,
                    "no se ha ingresado una consulta",
                    Toast.LENGTH_SHORT).show()

            }


        }
    }

    private fun cargarMisAnuncios() {
        anunciosArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.orderByChild("uid").equalTo(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    anunciosArrayList.clear()
                    for (ds in snapshot.children){
                        try {
                            val modeloAnuncio = ds.getValue(ModeloAnuncio::class.java)
                            anunciosArrayList.add(modeloAnuncio!!)

                        }catch (e:Exception){

                        }
                    }
                    anunciosAdaptador = AdaptadorAnuncio(mContext, anunciosArrayList)
                    binding.misAnunciosRv.adapter = anunciosAdaptador

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


}