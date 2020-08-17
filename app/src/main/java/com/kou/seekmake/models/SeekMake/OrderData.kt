package com.kou.seekmake.models.SeekMake

data class OrderData(
        val __v: Int,
        val _id: String,
        val adress: String,
        val city: String,
        val client: String,
        val createdAt: String,
        val createdDate: Long,
        val dateAccepted: Any,
        val declinedBy: List<Any>,
        val epaiseur: Any,
        val `file`: String,
        val firstname: String,
        val lastname: String,
        val machineOwner: Any,
        val phone: String,
        val priceByMachins: List<Any>,
        val quantite: Int,
        val status: String,
        val technique: String,
        val type: String,
        val updatedAt: String,
        val zip: String
)