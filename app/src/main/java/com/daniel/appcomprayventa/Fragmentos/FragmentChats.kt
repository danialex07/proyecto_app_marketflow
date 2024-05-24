package com.daniel.appcomprayventa.Fragmentos

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daniel.appcomprayventa.Adaptadores.AdaptadorChat
import com.daniel.appcomprayventa.Adaptadores.AdaptadorChats
import com.daniel.appcomprayventa.Modelo.ModeloChats
import com.daniel.appcomprayventa.R
import com.daniel.appcomprayventa.databinding.FragmentChatsBinding
import com.daniel.appcomprayventa.databinding.ItemChatsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentChats : Fragment() {

    private  lateinit var  binding: FragmentChatsBinding

    private lateinit var  firebaseAuth: FirebaseAuth
    private  var miUid = ""
    private lateinit var  chatArrayList: ArrayList<ModeloChats>
    private lateinit var adaptadorChats: AdaptadorChats
    private lateinit var mContext: Context


    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        miUid = "${firebaseAuth.uid}"
        cargarChats()

        binding.EtBuscar.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(filtro: CharSequence?, start: Int, before: Int, count: Int) {
                try{
                    val consulta = filtro.toString()
                    adaptadorChats.filter.filter(consulta)

                }catch (e:Exception){

                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun cargarChats() {
        chatArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatArrayList.clear()
                for (ds in snapshot.children){
                    val chatKey = "${ds.key}" //uidemisor_uidreceptor
                    if(chatKey.contains(miUid)){
                        val modeloChats = ModeloChats()
                        modeloChats.keyChat = chatKey
                        chatArrayList.add(modeloChats)

                    }
                }
                adaptadorChats = AdaptadorChats(mContext, chatArrayList)
                binding.chatsRv.adapter = adaptadorChats
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


}