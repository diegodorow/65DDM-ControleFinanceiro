package br.udesc.ddm.controlefinanceiro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import br.udesc.ddm.controlefinanceiro.databinding.FragmentCadastrarTipoLancamentoBinding
import br.udesc.ddm.controlefinanceiro.model.TipoLancamento
import br.udesc.ddm.controlefinanceiro.ui.activity.TIPO_ENTRADA
import br.udesc.ddm.controlefinanceiro.ui.activity.TIPO_SAIDA
import br.udesc.ddm.controlefinanceiro.viewModel.TipoLancamentoViewModel
import kotlinx.coroutines.launch

class CadastrarTipoLancamentoFragment : Fragment() {

    private var _binding: FragmentCadastrarTipoLancamentoBinding? = null
    private lateinit var tipoLancamentoViewModel: TipoLancamentoViewModel

    private val binding get() = _binding!!

    private lateinit var botaoCadastrar: Button
    private var tipoLancamentoId = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tipoLancamentoViewModel = ViewModelProvider(this).get(TipoLancamentoViewModel::class.java)

        _binding = FragmentCadastrarTipoLancamentoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        botaoCadastrar = binding.fragmentFormularioTipolancamentoBotaoSalvar

        configuraBotaoSalvar()
        configuraSpinner()
        return root
    }

    private fun configuraBotaoSalvar() {
        val botaoSalvar = binding.fragmentFormularioTipolancamentoBotaoSalvar

        botaoSalvar.setOnClickListener {
            lifecycleScope.launch {
                val tipoLancamentoNovo = criaTipoLancamento()

                if (tipoLancamentoNovo.nome.isNotEmpty()) {
                    tipoLancamentoViewModel.cadastrarTipoLancamento(tipoLancamentoNovo)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Preencha todos os campos!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    private fun criaTipoLancamento(): TipoLancamento {
        val campoNome = binding.fragmentCadastroTipolancamentoNome
        val nome = campoNome.text.toString()
        val acao = binding.spAcao.selectedItem.toString()

        return TipoLancamento(
            id = tipoLancamentoId,
            nome = nome,
            acao = acao
        )
    }

    private fun configuraSpinner() {
        val spTipoConta = binding.spAcao
        val listaTiposAcao = arrayOf(TIPO_ENTRADA, TIPO_SAIDA)
        val adapterTipoConta = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listaTiposAcao
        )
        adapterTipoConta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoConta.adapter = adapterTipoConta
    }
}