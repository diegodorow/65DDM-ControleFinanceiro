package br.udesc.ddm.controlefinanceiro.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import br.udesc.ddm.controlefinanceiro.databinding.FragmentCadastrarContaBinding
import br.udesc.ddm.controlefinanceiro.model.Conta
import br.udesc.ddm.controlefinanceiro.viewModel.ContaViewModel

class CadastrarContaFragment : Fragment() {

    private var _binding: FragmentCadastrarContaBinding? = null
    private lateinit var contaViewModel: ContaViewModel

    private val binding get() = _binding!!

    private lateinit var botaoCadastrar: Button
    private var contaId = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contaViewModel = ViewModelProvider(this).get(ContaViewModel::class.java)

        _binding = FragmentCadastrarContaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        botaoCadastrar = binding.fragmentFormularioContaBotaoSalvar

        configuraBotaoSalvar()
        return root
    }

    private fun configuraBotaoSalvar() {
        botaoCadastrar.setOnClickListener {
            val contaNova = criaConta()
            if (contaNova.nome.isNotEmpty()) {
                contaViewModel.cadastrarConta(contaNova.nome)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Preencha todos os campos!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            Navigation.findNavController(binding.root)
                .navigate(br.udesc.ddm.controlefinanceiro.R.id.nav_lista_contas_fragment)
        }
    }


    private fun criaConta(): Conta {
        val campoNome = binding.fragmentCadastroContaNome
        val nome = campoNome.text.toString()

        Log.i("CadastrarContaFragment", "criaConta $nome")
        return Conta(
            id = contaId,
            nome = nome
        )
    }


}