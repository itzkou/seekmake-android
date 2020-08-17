package com.kou.seekmake.models.SeekMake

data class UserSeek(
        val address: String = "",
        val city: String = "",
        val email: String = "",
        val firstname: String = "",
        val lastname: String = "",
        val password: String = "",
        val password_confirmation: String = "",
        val tel: String = "",
        val zip: String = ""
)