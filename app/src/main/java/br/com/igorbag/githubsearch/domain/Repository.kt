package br.com.igorbag.githubsearch.domain

import com.google.gson.annotations.SerializedName

data class Repository(
    val name: String,
    @SerializedName("html_url")
    val htmlUrl: String
)

fun main() {
    val meusRepositorios = listOf(
        Repository("Repositorio1", "https://github.com/estefaniapinheiro12/Projetoweb"),
        Repository("Repositorio2", "https://github.com/estefaniapinheiro12/bootstrap"),
        Repository("Repositorio3", "https://github.com/estefaniapinheiro12/jogo_da_velha")
    )

}