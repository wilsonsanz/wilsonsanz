package com.example.segura_control.script_post

data class PostResponseLogin(
    val action: String,
    val spreadsheet_id: String,
    val sheet: String,
    val rows: List<List<String>>
)
