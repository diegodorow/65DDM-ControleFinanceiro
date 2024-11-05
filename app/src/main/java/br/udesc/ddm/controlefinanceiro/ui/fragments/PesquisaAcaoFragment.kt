package br.udesc.ddm.controlefinanceiro.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.udesc.ddm.controlefinanceiro.databinding.FragmentPesquisaAcaoBinding
import br.udesc.ddm.controlefinanceiro.model.NovaRespostaApi
import br.udesc.ddm.controlefinanceiro.recyclerview.adapter.AcaoAPIAdapter
import br.udesc.ddm.controlefinanceiro.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PesquisaAcaoFragment : Fragment() {
    private var _binding: FragmentPesquisaAcaoBinding? = null
    private val binding get() = _binding!!

    private lateinit var retrofitInitializer: RetrofitInitializer
    private val listaAcoes = mutableListOf<String>()
    private lateinit var adapter: AcaoAPIAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPesquisaAcaoBinding.inflate(inflater, container, false)
        retrofitInitializer = RetrofitInitializer()

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
            val nomeAcao = binding.textInputLayoutAcao.toString()
            if (nomeAcao.isNotBlank()) {
                listaAcoes.clear()
                searchAcoes(nomeAcao)
            } else {
                Toast.makeText(requireContext(), "Informe o nome da ação", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun searchAcoes(nome: String) {
        var pagina = 1
        val service = retrofitInitializer.create()

        fun fetchPage() {
            Log.i("PesquisaAcaoFragment", "antes de fazer a call")
            val call = service.pesquisarAcao()
            Log.i("PesquisaAcaoFragment", "call: ${call.toString()}")
            call.enqueue(object : Callback<NovaRespostaApi> {
                override fun onResponse(
                    call: Call<NovaRespostaApi>,
                    response: Response<NovaRespostaApi>
                ) {

                    Log.i("PesquisaAcaoFragment", "onResponse")
                    if (response.isSuccessful) {
                        Log.i("PesquisaAcaoFragment", "isSuccessful")
                        response.body()?.let { respostaAPI ->

                            adapter.notifyDataSetChanged()
                            Log.i("PesquisaAcaoFragment", "notifyDataSetChanged")

                            if (respostaAPI.stocks.isNotEmpty()) {
                                listaAcoes.addAll(respostaAPI.stocks)
                                Log.i("PesquisaAcaoFragment", "isNotEmpty")
                                pagina++
                                fetchPage()
                            }
                        }
                    } else {
                        Log.i(
                            "PesquisaAcaoFragment", "else isSuccessful requireContext "
                                    + requireContext().toString() + ", response.errorBody() " + response.errorBody()
                                .toString()
                        )
                        Toast.makeText(requireContext(), response.message(), Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onFailure(call: Call<NovaRespostaApi>, t: Throwable) {
                    Log.i("PesquisaAcaoFragment", "else isSuccessful requireContext ${t.message}")
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