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
import br.udesc.ddm.controlefinanceiro.databinding.FragmentListarLancamentoBinding
import br.udesc.ddm.controlefinanceiro.model.Lancamento
import br.udesc.ddm.controlefinanceiro.recyclerview.adapter.LancamentoAdapter
import br.udesc.ddm.controlefinanceiro.ui.activity.CHAVE_LANCAMENTO_ID
import br.udesc.ddm.controlefinanceiro.ui.activity.DetalhesLancamentoActivity
import br.udesc.ddm.controlefinanceiro.viewModel.LancamentoViewModel

class ListarLancamentoFragment : Fragment() {

    private var _binding: FragmentListarLancamentoBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: LancamentoAdapter

    private lateinit var lancamentoViewModel: LancamentoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lancamentoViewModel = ViewModelProvider(this).get(LancamentoViewModel::class.java)

        _binding = FragmentListarLancamentoBinding.inflate(inflater, container, false)
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
        val recyclerView = binding.fragmentListaLancamentosRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lancamentoViewModel.lancamentos.observe(viewLifecycleOwner) { lancamentos ->
            if (lancamentos != null) {
                adapter = LancamentoAdapter(lancamentos)
                recyclerView.adapter = adapter

                adapter.quandoClicaNoItem = {
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.nav_detalhes_lancamento)

                    val intent = Intent(requireContext(), DetalhesLancamentoActivity::class.java)
                    intent.putExtra(CHAVE_LANCAMENTO_ID, it.id)
                    startActivity(intent)
                }

                val listaLancamentos: List<Lancamento> =
                    lancamentoViewModel.buscarTodosLancamentos()
                adapter.atualiza(listaLancamentos)
            }
        }
    }

    private fun configuraFab() {
        val fab = binding.fragmentListaLancamentosFab
        fab.setOnClickListener {
            vaiParaFormularioLancamento()
        }
    }

    private fun vaiParaFormularioLancamento() {
        Navigation.findNavController(binding.root)
            .navigate(br.udesc.ddm.controlefinanceiro.R.id.nav_cadastrar_lancamento)
    }
}