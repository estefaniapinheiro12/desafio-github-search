package br.com.igorbag.githubsearch.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository

class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        setupRetrofit()
        getAllReposByUserName()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
    nomeUsuario = findViewById(R.id.et_nome_usuario)
    btnConfirmar = findViewById(R.id.btn_confirmar)
    listaRepositories = findViewById(R.id.rv_lista_repositories)

    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
       btn_confirmar.setOnClickListener {
        saveUserLocal()
        }
    }


    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal() {

 val usuario = nomeUsuario.text.toString()
    val sharedPreferences = getSharedPreferences("MyPrefer", MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("usuario", usuario)
    editor.apply()

    }

    private fun showUserName() {
    val sharedPreferences = getSharedPreferences("MyPrefer", MODE_PRIVATE)
    val usuarioSalvo = sharedPreferences.getString("usuario", "")
    nomeUsuario.setText(usuarioSalvo)    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
       val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    githubApi = retrofit.create(GitHubService::class.java)
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName() {

         val usuario = nomeUsuario.text.toString()
    githubApi.getAllRepositoriesByUser(usuario).enqueue(object : Callback<List<Repository>> {
        override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
            if (response.isSuccessful) {
                val repositories = response.body()
                repositories?.let {
                    setupAdapter(it)
                }
            }
        }
        override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
        
        }
    })

    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        
    val adapter = RepositoryAdapter(list, ::shareRepositoryLink, ::openBrowser)
    listaRepositories.layoutManager = LinearLayoutManager(this)
    listaRepositories.adapter = adapter

    }


    // Metodo responsavel por compartilhar o link do repositorio selecionado
 

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = repositories[position]

   
        holder.shareButton.setOnClickListener {
            shareRepositoryLink(repository.htmlUrl)
     }
}

    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio

    
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val repository = repositories[position]

    holder.openBrowserButton.setOnClickListener {
        openBrowser(repository.htmlUrl)
    }
}

    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}