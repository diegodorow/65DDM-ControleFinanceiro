package br.udesc.ddm.controlefinanceiro.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import br.udesc.ddm.controlefinanceiro.R
import br.udesc.ddm.controlefinanceiro.database.dao.ContaDao
import br.udesc.ddm.controlefinanceiro.database.repository.ContasRepository
import br.udesc.ddm.controlefinanceiro.databinding.FragmentHomeBinding
import br.udesc.ddm.controlefinanceiro.recyclerview.adapter.ListaContasAdapter
import br.udesc.ddm.controlefinanceiro.recyclerview.adapter.MyAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private val adapter = ListaContasAdapter(context = null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Inicialização do ViewModel
//        val homeViewModel = ViewModelProvider(this, ContaViewModelFactory(ContasRepository(repository))).get(
//            HomeViewModel::class.java
//        )
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        configuraFab()

        // Referencia para o RecyclerView
        val recyclerView = binding.fragmentHomeListaContasRecyclerView

        // Lista de dados
        val listaDeContas = homeViewModel.buscaTodasContas()
        Log.i("HomeFragment", "lista de contas:  $listaDeContas")

        // Configurando o LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Definindo o Adapter
        recyclerView.adapter = MyAdapter(listaDeContas)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configuraFab() {
        Log.i("ListaContasFragment", "configuraFab")
        val fab = binding.fragmentListaContasFab
        fab.setOnClickListener {
            vaiParaFormularioConta()
        }
    }

    private fun vaiParaFormularioConta() {
        Log.i("ListaContasFragment", "vaiParaFormularioConta")
        Navigation.findNavController(binding.root).navigate(R.id.nav_cadastrar_conta)
//        Navigation.findNavController(binding.root).navigate(R.id.nav_gallery)
    }
}