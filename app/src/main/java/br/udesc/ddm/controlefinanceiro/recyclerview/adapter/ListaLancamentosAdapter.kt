package br.udesc.ddm.controlefinanceiro.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.udesc.ddm.controlefinanceiro.databinding.ItemLancamentoBinding
import br.udesc.ddm.controlefinanceiro.model.Lancamento

class ListaLancamentosAdapter(
    private val context: Context,
    lancamentos: List<Lancamento> = emptyList(),
    var quandoClicaNoItem: (lancamento: Lancamento) -> Unit = {}
) : RecyclerView.Adapter<ListaLancamentosAdapter.ViewHolder>() {

    private val lancamentos = lancamentos.toMutableList()

    inner class ViewHolder(private val binding: ItemLancamentoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var lancamento: Lancamento

        init {
            itemView.setOnClickListener {
                if (::lancamento.isInitialized) {
                    quandoClicaNoItem(lancamento)
                }
            }
        }

        fun vincula(lancamento: Lancamento) {
            this.lancamento = lancamento
            val nome = binding.lancamentoItemNome
            nome.text = lancamento.nome

            val conta = binding.lancamentoItemBanco
            conta.text = lancamento.banco

            val tipoGasto = binding.lancamentoItemTipolancamento
            tipoGasto.text = lancamento.tipo

            val valor = binding.lancamentoItemValor
            valor.text = lancamento.valor.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemLancamentoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = lancamentos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lancamento = lancamentos[position]
        holder.vincula(lancamento)
    }

    fun atualiza(lancamentos: List<Lancamento>) {
        this.lancamentos.clear()
        this.lancamentos.addAll(lancamentos)
        notifyDataSetChanged()
    }

}