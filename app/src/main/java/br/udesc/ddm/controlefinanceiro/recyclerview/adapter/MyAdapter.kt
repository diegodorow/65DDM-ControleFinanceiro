package br.udesc.ddm.controlefinanceiro.recyclerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.udesc.ddm.controlefinanceiro.databinding.ItemContaBinding
import br.udesc.ddm.controlefinanceiro.extensions.tentaCarregarImagem
import br.udesc.ddm.controlefinanceiro.model.Conta
import kotlinx.coroutines.flow.Flow

class MyAdapter(
    private val itemList: Flow<List<Conta>>?,
    contas: List<Conta> = emptyList(),
    var quandoClicaNoItem: (conta: Conta) -> Unit = {}
) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private val contas = contas.toMutableList()

    inner class ViewHolder(private val binding: ItemContaBinding) :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemContaBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = contas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conta = contas[position]
        holder.vincula(conta)
    }

    fun atualiza(contas: List<Conta>) {
        this.contas.clear()
        this.contas.addAll(contas)
        notifyDataSetChanged()
    }
    /*class MyViewHolder(itemView: View, private val binding: ItemContaBinding) :
        RecyclerView.ViewHolder(itemView) {
        val textView: TextView = binding.contaItemNome
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conta, parent, false)

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemContaBinding.inflate(inflater, parent, false)
        return MyViewHolder(view, binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = itemList[position]
    }

    override fun getItemCount() = itemList.size*/
}