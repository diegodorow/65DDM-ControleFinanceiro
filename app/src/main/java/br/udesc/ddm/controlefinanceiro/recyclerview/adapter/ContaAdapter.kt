package br.udesc.ddm.controlefinanceiro.recyclerview.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.udesc.ddm.controlefinanceiro.databinding.ItemContaBinding
import br.udesc.ddm.controlefinanceiro.extensions.tentaCarregarImagem
import br.udesc.ddm.controlefinanceiro.model.Conta

class ContaAdapter(
    contas: List<Conta> = emptyList(),
    var quandoClicaNoItem: (conta: Conta) -> Unit = {}
) :
    RecyclerView.Adapter<ContaAdapter.ContaViewHolder>() {

    private val contas = contas.toMutableList()

    inner class ContaViewHolder(private val binding: ItemContaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var conta: Conta

        init {
            itemView.setOnClickListener {
                if (::conta.isInitialized) {
                    quandoClicaNoItem(conta)
                }
            }
        }

        fun vincula(conta: Conta) {
            this.conta = conta
            val nome = binding.contaItemNome
            nome.text = conta.nome

            val saldo = binding.contaItemSaldo
            saldo.text = "R$ 0.00"

            val visibilidade = if (conta.imagem != null) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.imageView.visibility = visibilidade
            binding.imageView.tentaCarregarImagem(conta.imagem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemContaBinding.inflate(inflater, parent, false)
        return ContaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContaViewHolder, position: Int) {
        val conta = contas[position]
        holder.vincula(conta)
    }

    override fun getItemCount(): Int = contas.size

    @SuppressLint("NotifyDataSetChanged")
    fun atualiza(contas: List<Conta>) {
        this.contas.clear()
        this.contas.addAll(contas)
        notifyDataSetChanged()
    }

}
