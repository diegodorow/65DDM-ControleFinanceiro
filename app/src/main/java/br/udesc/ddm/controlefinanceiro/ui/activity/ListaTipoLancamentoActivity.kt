package br.udesc.ddm.controlefinanceiro.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.udesc.ddm.controlefinanceiro.R
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.databinding.ActivityListaTipoLancamentoBinding
import br.udesc.ddm.controlefinanceiro.recyclerview.adapter.ListaTiposLancamentosAdapter
import kotlinx.coroutines.launch

class ListaTipoLancamentoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityListaTipoLancamentoBinding.inflate(layoutInflater)
    }

    private val tipoLancamentoDao by lazy {
        val db = AppDatabase.instancia(this)
        db.tipoLancamentoDao()
    }

    private val adapter = ListaTiposLancamentosAdapter(context = this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Tipos de lançamentos"
        configuraRecyclerView()
        configuraFab()
        lifecycleScope.launch {
            launch {
                buscaTodosTipos()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lista_projeto, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun configuraFab() {
        val fab = binding.activityListaTiposlancamentosFab
        fab.setOnClickListener {
            vaiParaFormularioTipoLancamento()
        }
    }

    private fun vaiParaFormularioTipoLancamento() {
        Log.i("ListaTipoLancamentoActivity", "vaiParaFormularioTipoLancamento")
        val intent = Intent(this, CadastrarTipoLancamentoActivity::class.java)
        startActivity(intent)
    }

    private suspend fun buscaTodosTipos() {
//        tipoLancamentoDao.buscaTodos().collect { tiposLancamentos ->
//            adapter.atualiza(tiposLancamentos)
//        }
    }

    private fun configuraRecyclerView() {
        val recyclerView = binding.activityListaTiposlancamentosRecyclerView
        recyclerView.adapter = adapter
        adapter.quandoClicaNoItem = {
            val intent = Intent(
                this,
                DetalhesTipoLancamentoActivity::class.java
            ).apply {
                putExtra(CHAVE_TIPOLANCAMENTO_ID, it.id)
            }
            startActivity(intent)
        }
    }
}