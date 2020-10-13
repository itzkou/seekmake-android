package com.kou.seekmake.screens.profile.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kou.seekmake.R
import com.kou.seekmake.models.SeekMake.Demand
import com.kou.seekmake.screens.common.SimpleCallback
import kotlinx.android.synthetic.main.quote_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class QuotesAdapter(private val listener: Listener) : RecyclerView.Adapter<QuotesAdapter.ViewHolder>() {
    interface Listener {
        fun updateDemand(demandId: String)

    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var quotes = listOf<Demand>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.quote_item, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount() = quotes.size

    fun updateDemands(newDemands: List<Demand>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.quotes, newDemands) { it._id })
        this.quotes = newDemands
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quote = quotes[position]
        with(holder.view) {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(quote.createdDate)
            tx_qty.text = quote.quantite.toString()
            tx_date.text = sdf.format(netDate)
            tx_technqiue.setImageResource(when (quote.technique) {
                "laser" -> R.drawable.laser_small
                "impression" -> R.drawable.small_3d
                else -> R.drawable.frais_small
            }
            )
            tvStatus.text = quote.status
            tvType.text = quote.type

            imTech.setImageResource(when (quote.technique) {
                "laser" -> R.drawable.bg_laser
                "impression" -> R.drawable.bg_3d
                else -> R.drawable.bg_fraisage
            }
            )

            imMaterial.setImageResource(when (quote.matiere.toLowerCase()) {
                "acier" -> R.drawable.ic_acier
                "alucobond" -> R.drawable.ic_aluco
                else -> R.drawable.ic_bois
            }
            )

            val qtyCount = holder.view.context.resources.getQuantityString(
                    R.plurals.qty_count, quote.quantite, quote.quantite)

            tvQtycount.text = qtyCount


        }


    }


}