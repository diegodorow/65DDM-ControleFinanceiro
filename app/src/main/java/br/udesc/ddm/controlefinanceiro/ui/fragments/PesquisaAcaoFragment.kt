package br.udesc.ddm.controlefinanceiro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.udesc.ddm.controlefinanceiro.databinding.FragmentPesquisaAcaoBinding
import br.udesc.ddm.controlefinanceiro.model.AcaoAPI
import br.udesc.ddm.controlefinanceiro.model.RespostaAPI
import br.udesc.ddm.controlefinanceiro.recyclerview.adapter.AcaoAPIAdapter
import br.udesc.ddm.controlefinanceiro.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PesquisaAcaoFragment : Fragment() {
    private var _binding: FragmentPesquisaAcaoBinding? = null
    private val binding get() = _binding!!

    private lateinit var retrofitInitializer: RetrofitInitializer
    private val listaAcoes = mutableListOf<AcaoAPI>()
    private lateinit var adapter: AcaoAPIAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPesquisaAcaoBinding.inflate(inflater, container, false)
        retrofitInitializer = RetrofitInitializer("d8pa2h1pwVhC41Cv8sNiYS")

        setupRecyclerView()
        setupSearchButton()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = AcaoAPIAdapter(listaAcoes)
        binding.recyclerViewAcao.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewAcao.adapter = adapter
    }

    private fun setupSearchButton() {
        binding.btPesquisarAcao.setOnClickListener {
            val nomeAcao = binding.fragmentTextNomeAcao.text.toString()
            if (nomeAcao.isNotEmpty()) {
                listaAcoes.clear()
                searchAcoes(nomeAcao)
            } else {
                Toast.makeText(requireContext(), "Informe o nome da ação", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun searchAcoes(nome: String) {
        val service = retrofitInitializer.create()

        fun fetchPage() {
            val call = service.pesquisarAcao(nome)
            call.enqueue(object : Callback<RespostaAPI> {
                override fun onResponse(
                    call: Call<RespostaAPI>,
                    response: Response<RespostaAPI>
                ) {

                    if (response.isSuccessful) {
                        response.body()?.let { respostaAPI ->
                            listaAcoes.addAll(respostaAPI.results)
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        Toast.makeText(requireContext(), response.message(), Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onFailure(call: Call<RespostaAPI>, t: Throwable) {
                    System.out.println(t.toString());
                    Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        fetchPage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
