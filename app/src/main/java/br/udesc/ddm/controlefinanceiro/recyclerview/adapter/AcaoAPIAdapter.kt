package br.udesc.ddm.controlefinanceiro.recyclerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.udesc.ddm.controlefinanceiro.R
import br.udesc.ddm.controlefinanceiro.model.AcaoAPI

class AcaoAPIAdapter(
    private val acoes: MutableList<AcaoAPI>
) : RecyclerView.Adapter<AcaoAPIAdapter.AcaoViewHolder>() {

    inner class AcaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val acaoNome: TextView = itemView.findViewById(R.id.acaoNome)
        val acaoDescricao: TextView = itemView.findViewById(R.id.acaoDescricao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_acao_api, parent, false)
        return AcaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AcaoViewHolder, position: Int) {
        val acao = acoes[position]
        holder.acaoNome.text = acao.shortName
        holder.acaoDescricao.text = acao.longName
    }

    override fun getItemCount(): Int = acoes.size

//    fun adicionarAcao(novasAcoes: List<AcaoAPI>) {
//        acoes.addAll(novasAcoes)
//        notifyDataSetChanged()
//    }
}