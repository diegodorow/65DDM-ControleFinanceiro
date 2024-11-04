package br.udesc.ddm.controlefinanceiro.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.udesc.ddm.controlefinanceiro.R
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.databinding.ActivityListaLancamentoBinding
import br.udesc.ddm.controlefinanceiro.recyclerview.adapter.ListaLancamentosAdapter
import kotlinx.coroutines.launch

class ListaLancamentoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityListaLancamentoBinding.inflate(layoutInflater)
    }

    private val lancamentoDao by lazy {
        val db = AppDatabase.instancia(this)
        db.lancamentoDao()
    }

    private val adapter = ListaLancamentosAdapter(context = this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        title = "Lançamentos"
        configuraRecyclerView()
        configuraFab()
        lifecycleScope.launch {
            launch {
                buscaTodosLancamentos()
            }
        }
    }

    private fun configuraFab() {
        val fab = binding.activityListaLancamentosFab
        fab.setOnClickListener {
            vaiParaFormularioLancamento()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lista_projeto, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun vaiParaFormularioLancamento() {
        val intent = Intent(this, CadastrarLancamentoActivity::class.java)
        startActivity(intent)
    }

    private suspend fun buscaTodosLancamentos() {
//        lancamentoDao.buscaTodos().collect { lancamentos ->
//            adapter.atualiza(lancamentos)
//        }
    }

    private fun configuraRecyclerView() {
        val recyclerView = binding.activityListaLancamentosRecyclerView
        recyclerView.adapter = adapter
        adapter.quandoClicaNoItem = {
            val intent = Intent(
                this,
                DetalhesLancamentoActivity::class.java
            ).apply {
                putExtra(CHAVE_LANCAMENTO_ID, it.id)
            }
            startActivity(intent)
        }
    }
}