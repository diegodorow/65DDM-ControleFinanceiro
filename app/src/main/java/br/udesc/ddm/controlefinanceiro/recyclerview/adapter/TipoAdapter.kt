package br.udesc.ddm.controlefinanceiro.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.udesc.ddm.controlefinanceiro.databinding.ItemTipolancamentoBinding
import br.udesc.ddm.controlefinanceiro.model.TipoLancamento

class TipoAdapter(
    tiposLancamentos: List<TipoLancamento> = emptyList(),
    var quandoClicaNoItem: (tipoLancamento: TipoLancamento) -> Unit = {}
) : RecyclerView.Adapter<TipoAdapter.TipoViewHolder>() {

    private val tiposLancamentos = tiposLancamentos.toMutableList()

    inner class TipoViewHolder(private val binding: ItemTipolancamentoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var tipoLancamento: TipoLancamento

        init {
            itemView.setOnClickListener {
                if (::tipoLancamento.isInitialized) {
                    quandoClicaNoItem(tipoLancamento)
                }
            }
        }

        fun vincula(tipoLancamento: TipoLancamento) {
            this.tipoLancamento = tipoLancamento
            val nome = binding.tipolancamentoItemNome
            val acao = binding.tipolancamentoItemAcao
            nome.text = tipoLancamento.nome
            acao.text = tipoLancamento.acao
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTipolancamentoBinding.inflate(inflater, parent, false)
        return TipoViewHolder(binding)
    }

    override fun getItemCount(): Int = tiposLancamentos.size

    override fun onBindViewHolder(holder: TipoViewHolder, position: Int) {
        val tipoLancamento = tiposLancamentos[position]
        holder.vincula(tipoLancamento)
    }

    fun atualiza(tiposLancamentos: List<TipoLancamento>) {
        this.tiposLancamentos.clear()
        this.tiposLancamentos.addAll(tiposLancamentos)
        notifyDataSetChanged()
    }
}