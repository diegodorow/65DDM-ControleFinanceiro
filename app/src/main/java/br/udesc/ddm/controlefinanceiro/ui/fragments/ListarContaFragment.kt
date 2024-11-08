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
import br.udesc.ddm.controlefinanceiro.databinding.FragmentListarContaBinding
import br.udesc.ddm.controlefinanceiro.model.Conta
import br.udesc.ddm.controlefinanceiro.recyclerview.adapter.ContaAdapter
import br.udesc.ddm.controlefinanceiro.ui.activity.CHAVE_CONTA_ID
import br.udesc.ddm.controlefinanceiro.ui.activity.DetalhesContaActivity
import br.udesc.ddm.controlefinanceiro.viewModel.ContaViewModel


class ListarContaFragment : Fragment() {

    private var _binding: FragmentListarContaBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ContaAdapter

    private lateinit var contaViewModel: ContaViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        contaViewModel = ViewModelProvider(this).get(ContaViewModel::class.java)

        _binding = FragmentListarContaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        atualizaTela()
        configuraFab()
        return root
    }

    override fun onResume() {
        super.onResume()
        atualizaTela()
    }

    fun atualizaTela() {
        val recyclerView = binding.fragmentListaContasRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        contaViewModel.contas.observe(viewLifecycleOwner) { contas ->
            if (contas != null) {
                adapter = ContaAdapter(contas)
                recyclerView.adapter = adapter

                adapter.quandoClicaNoItem = {
                    Navigation.findNavController(binding.root)
                        .navigate(br.udesc.ddm.controlefinanceiro.R.id.nav_detalhes_conta)

                    val intent = Intent(requireContext(), DetalhesContaActivity::class.java)
                    intent.putExtra(CHAVE_CONTA_ID, it.id)
                    startActivity(intent)
                }
                val listaContas: List<Conta> = contaViewModel.buscarTodasContas()
                adapter.atualiza(listaContas)
            }
        }
    }

    private fun configuraFab() {
        val fab = binding.fragmentListaContasFab
        fab.setOnClickListener {
            vaiParaFormularioConta()
        }
    }

    private fun vaiParaFormularioConta() {
        Navigation.findNavController(binding.root)
            .navigate(br.udesc.ddm.controlefinanceiro.R.id.nav_cadastrar_conta)
    }
}