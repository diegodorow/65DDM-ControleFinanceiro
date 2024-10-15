package br.udesc.ddm.controlefinanceiro.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.udesc.ddm.controlefinanceiro.R
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.databinding.ActivityListaContaBinding
import br.udesc.ddm.controlefinanceiro.recyclerview.adapter.ListaContasAdapter
import kotlinx.coroutines.launch

class ListaContaActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityListaContaBinding.inflate(layoutInflater)
    }

    private val contaDao by lazy {
        val db = AppDatabase.instancia(this)
        db.contaDao()
    }

    private val adapter = ListaContasAdapter(context = this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Contas"
        configuraRecyclerView()
        configuraFab()
        lifecycleScope.launch {
            launch {
                buscaTodasContas()
            }
        }
    }

    private fun configuraFab() {
        Log.i("ListaContaActivity", "configuraFab")
        val fab = binding.activityListaContasFab
        fab.setOnClickListener {
            vaiParaFormularioConta()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.i("ListaContaActivity", "onCreateOptionsMenu")
        menuInflater.inflate(R.menu.menu_lista_projeto, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun vaiParaFormularioConta() {
        Log.i("ListaContaActivity", "vaiParaFormularioConta")
        val intent = Intent(this, CadastrarContaActivity::class.java)
        startActivity(intent)
    }

    private suspend fun buscaTodasContas() {
        Log.i("ListaContaActivity", "buscaTodasContas")
        contaDao.buscaTodas().collect { contas ->
            adapter.atualiza(contas)
        }
    }

    private fun configuraRecyclerView() {
        val recyclerView = binding.activityListaContasRecyclerView
        recyclerView.adapter = adapter
        adapter.quandoClicaNoItem = {
            val intent = Intent(
                this,
                DetalhesContaActivity::class.java
            ).apply {
                putExtra(CHAVE_CONTA_ID, it.id)
            }
            startActivity(intent)
        }
    }
}