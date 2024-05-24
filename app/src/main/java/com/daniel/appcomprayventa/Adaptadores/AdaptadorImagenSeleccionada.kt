package com.daniel.appcomprayventa.Adaptadores

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.daniel.appcomprayventa.Modelo.ModeloImageSeleccionada
import com.daniel.appcomprayventa.R
import com.daniel.appcomprayventa.databinding.ItemImagenesSeleccionadasBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AdaptadorImagenSeleccionada(
    private val content : Context,
    private val imagenesSelecArrayList : ArrayList<ModeloImageSeleccionada>,
    private val idAnuncio : String
): Adapter<AdaptadorImagenSeleccionada.HolderImagenSeleccionada>() {


    private lateinit var binding : ItemImagenesSeleccionadasBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagenSeleccionada {
        binding = ItemImagenesSeleccionadasBinding.inflate(LayoutInflater.from(content),parent,false)
        return HolderImagenSeleccionada(binding.root)
    }

    override fun getItemCount(): Int {
        return  imagenesSelecArrayList.size
    }

    override fun onBindViewHolder(holder: HolderImagenSeleccionada, position: Int) {
        val modelo = imagenesSelecArrayList[position]

        if (modelo.deInternet){
            /*
            haremos lectura  de las imagenes traidas desde Firebase
             */
            try {
                val  imagenUrl = modelo.imageUrl
                Glide.with(content)
                    .load(imagenUrl)
                    .placeholder(R.drawable.item_imagen)
                    .into(binding.itemImagen)

            }catch (e:Exception){

            }
        }else{
            /*
            haremos lectura de las imagenes seleccionadas desde la galeria o tomadas desde la camara
             */
            try {
                val imagenUri = modelo.imagenUri
                Glide.with(content)
                    .load(imagenUri)
                    .placeholder(R.drawable.item_imagen)
                    .into(holder.item_imagen)

            }catch (e:Exception){

            }

        }

        holder.btn_cerrar.setOnClickListener {
            if (modelo.deInternet){
                //declarar las vistas del diseno
                val Btn_si : MaterialButton
                val Btn_no : MaterialButton
                val dialog = Dialog(content)

                dialog.setContentView(R.layout.cuadro_d_eliminar_imagen)

                Btn_si = dialog.findViewById(R.id.Btn_si)
                Btn_no = dialog.findViewById(R.id.Btn_no)

                Btn_si.setOnClickListener {
                    eliminarImgFirebase(modelo, holder, position)
                    dialog.dismiss()

                }
                Btn_no.setOnClickListener {
                    dialog.dismiss()

                }
                dialog.show()
                dialog.setCanceledOnTouchOutside(false)


            }else{
                imagenesSelecArrayList.remove(modelo)
                notifyDataSetChanged()

            }

        }
    }

    private fun eliminarImgFirebase(modelo: ModeloImageSeleccionada, holder: AdaptadorImagenSeleccionada.HolderImagenSeleccionada, position: Int) {
        val idImagen = modelo.id

        /* la imagen se eliminara en la base de datos - en el reladtime */
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(idAnuncio).child("Imagenes").child(idImagen)
            .removeValue()
            .addOnSuccessListener {
                try {
                    imagenesSelecArrayList.remove(modelo)
                    eliminarImgStorage(modelo) /* la imagen tambien se eliminara del storage */
                    notifyItemRemoved(position)

                }catch (e:Exception){

                }

            }
            .addOnFailureListener {e->
                Toast.makeText(content,"${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

    private fun eliminarImgStorage(modelo: ModeloImageSeleccionada) {
        val rutaImagen = "Anuncios/"+modelo.id


        val ref = FirebaseStorage.getInstance().getReference(rutaImagen)
        ref.delete()
            .addOnSuccessListener {
                Toast.makeText(content,"la imagen se ha eliminado",Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {e->
                Toast.makeText(content,"${e.message}",Toast.LENGTH_SHORT).show()

            }

    }

    inner class HolderImagenSeleccionada(itemView : View) : ViewHolder(itemView){
        var item_imagen = binding.itemImagen
        var btn_cerrar = binding.cerrarItem
    }


}