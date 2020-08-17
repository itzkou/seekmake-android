package com.kou.seekmake.models.SeekMake

data class PwdRequest(
        val new: String,
        val new_confirmation: String,
        val old: String
)