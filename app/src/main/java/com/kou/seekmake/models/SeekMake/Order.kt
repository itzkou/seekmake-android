package com.kou.seekmake.models.SeekMake

data class Order(
        val adress: String,
        val city: String,
        val client: String,
        val file: String,
        val firstname: String,
        val lastname: String,
        val phone: String,
        val technique: String,
        val type: String,
        val zip: String,
        val epaiseur: String? = "", ///extra criteria
        val matiere: String? = "",
        val quantite: Int? = 1,
        val resolution: String? = ""
)