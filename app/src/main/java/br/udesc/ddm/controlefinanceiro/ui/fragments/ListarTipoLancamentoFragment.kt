package br.udesc.ddm.controlefinanceiro.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import br.udesc.ddm.controlefinanceiro.R
import br.udesc.ddm.controlefinanceiro.databinding.FragmentListarTipoLancamentoBinding
import br.udesc.ddm.controlefinanceiro.model.TipoLancamento
import br.udesc.ddm.controlefinanceiro.recyclerview.adapter.TipoAdapter
import br.udesc.ddm.controlefinanceiro.ui.activity.CHAVE_TIPOLANCAMENTO_ID
import br.udesc.ddm.controlefinanceiro.ui.activity.DetalhesTipoLancamentoActivity
import br.udesc.ddm.controlefinanceiro.viewModel.TipoLancamentoViewModel

class ListarTipoLancamentoFragment : Fragment() {

    private var _binding: FragmentListarTipoLancamentoBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: TipoAdapter

    private lateinit var tipoLancamentoViewModel: TipoLancamentoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tipoLancamentoViewModel =
            ViewModelProvider(this).get(TipoLancamentoViewModel::class.java)

        _binding = FragmentListarTipoLancamentoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        atualizaTela()

        configuraFab()
        return root
    }

    override fun onResume() {
        super.onResume()
        atualizaTela()
    }

    private fun atualizaTela() {
        val recyclerView = binding.fragmentListaTipoLancamentoRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        tipoLancamentoViewModel.tipos.observe(viewLifecycleOwner) { tipos ->
            if (tipos != null) {
                adapter = TipoAdapter(tipos)
                recyclerView.adapter = adapter

                adapter.quandoClicaNoItem = {
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.nav_detalhes_tipo_lancamento)

                    val intent =
                        Intent(requireContext(), DetalhesTipoLancamentoActivity::class.java)
                    intent.putExtra(CHAVE_TIPOLANCAMENTO_ID, it.id)
                    startActivity(intent)
                }

                val listaTipos: List<TipoLancamento> =
                    tipoLancamentoViewModel.buscarTodosTipos()
                adapter.atualiza(listaTipos)
            }
        }
    }

    private fun configuraFab() {
        val fab = binding.fragmentListaTipoLancamentoFab
        fab.setOnClickListener {
            vaiParaFormularioTipoLancamento()
        }
    }

    private fun vaiParaFormularioTipoLancamento() {
        Navigation.findNavController(binding.root)
            .navigate(br.udesc.ddm.controlefinanceiro.R.id.nav_cadastrar_tipo_lancamento)
    }
}