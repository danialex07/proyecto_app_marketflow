package com.daniel.appcomprayventa

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.daniel.appcomprayventa.databinding.ActivityEditarPerfilBinding
import com.daniel.appcomprayventa.databinding.FragmentCuentaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.FirebaseStorage.*



class EditarPerfil : AppCompatActivity() {


    private lateinit var  binding: ActivityEditarPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var imageUri : Uri?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cargarInfo()
        binding.BtnActualizar.setOnClickListener {
            validarInfo()
        }
        binding.FABCambiarImg.setOnClickListener{
            selec_imagen_de()
        }
    }
    private  var nombres=""
    private var f_nac=""
    private var codigo=""
    private var telefono=""


    private fun validarInfo() {
        nombres = binding.EtNombres.text.toString().trim()
        f_nac = binding.EtFNac.text.toString().trim()
        codigo = binding.selectorCod.selectedCountryCodeWithPlus
        telefono = binding.EtTelefono.text.toString().trim()

        if (nombres.isEmpty()){
            Toast.makeText(this,"Ingrese sus nombres",Toast.LENGTH_SHORT).show()

        }else if(f_nac.isEmpty()){
            Toast.makeText(this,"Ingrese su fecha de nacimiento",Toast.LENGTH_SHORT).show()

        }else if(codigo.isEmpty()){
            Toast.makeText(this,"Ingrese sus nombres",Toast.LENGTH_SHORT).show()
        }else if(telefono.isEmpty()){
            Toast.makeText(this,"ingrese un telefono",Toast.LENGTH_SHORT).show()
        }else{
            actualizarInfo()
        }
    }

    private fun actualizarInfo() {
        progressDialog.setMessage("Actualizando informacion")
        val hashMap : HashMap<String, Any> = HashMap()

        hashMap["nombres"] = "${nombres}"
        hashMap["fecha_nac"] = "${f_nac}"
        hashMap["codigoTelefono"] = "${codigo}"
        hashMap["telefono"] = "${telefono}"

        val ref =  FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Se actualizo su informacion",Toast.LENGTH_SHORT).show()


            }.addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

    private fun cargarInfo(){
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val imagen = "${snapshot.child("urlImagenPerfil").value}"
                    val f_nac = "${snapshot.child("fecha_nac").value}"
                    val telefono = "${snapshot.child("telefono").value}"
                    val codTelefono = "${snapshot.child("codigoTelefono").value}"

                    //seteamos

                    binding.EtNombres.setText(nombres)
                    binding.EtFNac.setText(f_nac)
                    binding.EtTelefono.setText(telefono)

                    try{
                        Glide.with(applicationContext)
                            .load(imagen)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.imgPerfil)

                    }catch (e:Exception){
                        Toast.makeText(this@EditarPerfil,
                            "${e.message}",
                            Toast.LENGTH_SHORT).show()

                    }
                    try {
                        val codigo = codTelefono.replace("+","").toInt()
                        binding.selectorCod.setCountryForPhoneCode(codigo)

                    }catch (e:Exception){
                        Toast.makeText(this@EditarPerfil,
                            "${e.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun subirImagenStorage(){
        progressDialog.setMessage("Subiendo imagen a  Storage")
        progressDialog.show()


        val rutaImagen = "imagenesPerfil/" + firebaseAuth.uid
        //val ref = FirebaseStorage.getInstance().getReference(rutaImagen)
        val ref = getInstance().getReference(rutaImagen)
        ref.putFile(imageUri!!)
            .addOnSuccessListener{taskSnapShot->
                val uriTask = taskSnapShot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImagenCargada = uriTask.result.toString()
                if (uriTask.isSuccessful){
                    actualizarImagenBD(urlImagenCargada)
                }

            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

    private fun actualizarImagenBD(urlImagenCargada: String) {
        progressDialog.setMessage("Actualizando imagen")
        progressDialog.show()

        val hashMap : HashMap<String, Any> = HashMap()
        if (imageUri != null) {
            hashMap["urlImagenPerfil"] = urlImagenCargada
        }
        val  ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"Su imagen de perfil se a actualizado",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"${e.message}",Toast.LENGTH_SHORT).show()
            }

    }

    private  fun selec_imagen_de(){
        val popuMenu = PopupMenu(this,binding.FABCambiarImg)
        popuMenu.menu.add(Menu.NONE,1,1,"Camara")
        popuMenu.menu.add(Menu.NONE,2,2,"Galeria")


        popuMenu.show()


        popuMenu.setOnMenuItemClickListener { item->
            val itemId =item.itemId
            if (itemId == 1){
                //camara

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    concederPermisoCamara.launch(arrayOf(android.Manifest.permission.CAMERA))
                }else{
                    concederPermisoCamara.launch(arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ))
                }

            }else if (itemId == 2){
                //Galeria
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    imagenGaleria()

                }else{
                    //this.concederPermisoAlmacenamiento.launch(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    concederPermisoAlmacenamiento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

                }

            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun imagenCamara() {
        val contentValues =ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Titulo_imagen")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Descripcion_imagen")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)


        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        resultadoCamara_ARL.launch(intent)


    }


    private val resultadoCamara_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado->
            if(resultado.resultCode == Activity.RESULT_OK){


                subirImagenStorage()
                /*try {Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.img_perfil)
                    .into(binding.imgPerfil)

                }catch (e:Exception){

                }*/
            }else{
                Toast.makeText(
                    this,
                    "Cancelado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val concederPermisoCamara =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){resultado->
            var concedidoTodos = true
            for (seConcede in resultado.values){
                concedidoTodos = concedidoTodos && seConcede
            }

            if (concedidoTodos){
                imagenCamara()
            }else{
                Toast.makeText(
                    this,
                    "El permiso de la camara o almacenamiento ha sido  denegado, o ambas fueron denegadas",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    private  val concederPermisoAlmacenamiento =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){esConcedido->
            if (esConcedido){
                imagenGaleria()
            }else{
                Toast.makeText(
                    this,
                    "el permiso de la camara o almacenamiento ha sido denegada",
                    Toast.LENGTH_SHORT

                )
            }

        }

    private fun imagenGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type ="image/*"
        resultadoGaleria_ARL.launch(intent)
    }
    private val resultadoGaleria_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado->
            if (resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imageUri = data!!.data
                subirImagenStorage()



                /*try {Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.img_perfil)
                    .into(binding.imgPerfil)

                }catch (e:Exception){

                }*/

            }else{
                Toast.makeText(
                    this,
                    "Cancelado",
                    Toast.LENGTH_SHORT
                ).show()


            }

        }
}