package com.daniel.appcomprayventa.Modelo

import android.net.Uri

class ModeloImageSeleccionada {


    var id = ""
    var imagenUri :  Uri? = null
    var imageUrl  :   String? = null
    var deInternet = false


    constructor()
    constructor(id: String, imagenUri: Uri?, imageUrl: String?, deInternet: Boolean) {
        this.id = id
        this.imagenUri = imagenUri
        this.imageUrl = imageUrl
        this.deInternet = deInternet
    }


}