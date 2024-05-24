package com.daniel.appcomprayventa.Fragmentos

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.daniel.appcomprayventa.CambiarPassword
import com.daniel.appcomprayventa.Constantes
import com.daniel.appcomprayventa.EditarPerfil
import com.daniel.appcomprayventa.OpcionesLogin
import com.daniel.appcomprayventa.R
import com.daniel.appcomprayventa.databinding.FragmentCuentaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentCuenta : Fragment() {
    private lateinit var binding : FragmentCuentaBinding
    private lateinit var  firebaseAuth: FirebaseAuth
    private lateinit var mContext : Context
    private lateinit var progressDialog: ProgressDialog

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCuentaBinding.inflate(layoutInflater, container,false)
        return binding.root




    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(mContext)
        progressDialog.setTitle("Espere Por Favor")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        leerInfo()

        binding.BtnEditarPerfil.setOnClickListener {
            startActivity(Intent(mContext,EditarPerfil::class.java))
        }

        binding.BtnCambiarPass.setOnClickListener {
            startActivity(Intent(mContext, CambiarPassword::class.java))

        }
        binding.BtnVerificarCuenta.setOnClickListener {
            verificarCuenta()


        }

        binding.BtnCerrarSession.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(mContext, OpcionesLogin::class.java))
            activity?.finishAffinity()
        }
    }
    private fun leerInfo(){
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val email = "${snapshot.child("email").value}"
                    val imagen = "${snapshot.child("urlImagenPerfil").value}"
                    val f_nac = "${snapshot.child("fecha_nac").value}"
                    var tiempo ="${snapshot.child("tiempo").value}"
                    val telefono = "${snapshot.child("telefono").value}"
                    val codTelefono = "${snapshot.child("codigoTelefono").value}"
                    val proveedor = "${snapshot.child("proveedor").value}"


                    val  cod_tel = codTelefono+telefono
                    if(tiempo == "null"){
                        tiempo = "0"


                    }
                    val for_tiempo = Constantes.obtenerFecha(tiempo.toLong())

                    //seteamos informacion
                    binding.TvEmail.text = email
                    binding.TvNombres.text = nombres
                    binding.TvNacimiento.text = f_nac
                    binding.TvTelefono.text = cod_tel
                    binding.TvMiembro.text =  for_tiempo

                    //seteo de la imagen
                    try {
                        Glide.with(mContext)
                            .load(imagen)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.IvPerfil)

                    }catch (e:Exception){
                        Toast.makeText(
                            mContext,
                            "${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    if (proveedor == "Email"){
                        val esVerificado = firebaseAuth.currentUser!!.isEmailVerified
                        if (esVerificado){
                            //si el usuario esta verificad
                            binding.BtnVerificarCuenta.visibility = View.GONE
                            binding.TvEstadoCuenta.text = "Verificado"
                        }else{
                            //si el usuario no esta verificado
                            binding.BtnVerificarCuenta.visibility = View.VISIBLE
                            binding.TvEstadoCuenta.text = "No verificado"
                        }
                    }else{
                        //Usuario ingreso a la aplicacion con una cuenta de google
                        binding.BtnVerificarCuenta.visibility = View.GONE
                        binding.TvEstadoCuenta.text = "Verificado"
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")

                }
            })
    }

    private fun verificarCuenta(){
        progressDialog.setMessage("Enviando instrucciones de verificacion a su correo electronico")
        progressDialog.show()

        firebaseAuth.currentUser!!.sendEmailVerification()
            .addOnSuccessListener {
                Toast.makeText(
                    mContext,
                    "Las instrucciones  fueron enviadas a su correo registrado",
                    Toast.LENGTH_SHORT
                ).show()


            }
            .addOnFailureListener {e->
                Toast.makeText(
                    mContext,
                    "${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }
    }




}