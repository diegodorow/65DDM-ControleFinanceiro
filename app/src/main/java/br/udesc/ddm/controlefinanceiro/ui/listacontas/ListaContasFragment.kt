package br.udesc.ddm.controlefinanceiro.ui.listacontas

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
import br.udesc.ddm.controlefinanceiro.databinding.FragmentListaContasBinding
import br.udesc.ddm.controlefinanceiro.recyclerview.adapter.MyAdapter

class ListaContasFragment : Fragment() {

    private var _binding: FragmentListaContasBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val listaContaViewModel =
            ViewModelProvider(this).get(ListaContasViewModel::class.java)

        _binding = FragmentListaContasBinding.inflate(inflater, container, false)
        val root: View = binding.root

        configuraFab()
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
//        Navigation.findNavController(binding.root).navigate(R.id.nav_cadastrar_conta)
        Navigation.findNavController(binding.root).navigate(R.id.nav_home)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referência para o RecyclerView
        val recyclerView = binding.fragmentListaContasRecyclerView

        // Lista de dados
        val itemList = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")

        // Configurando o LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Definindo o Adapter
//        recyclerView.adapter = MyAdapter(itemList)
    }
}
