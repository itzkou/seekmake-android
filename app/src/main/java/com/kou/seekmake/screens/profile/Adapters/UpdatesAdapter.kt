package com.kou.seekmake.screens.profile.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kou.seekmake.R
import com.kou.seekmake.models.SeekMake.Demand
import com.kou.seekmake.screens.common.SimpleCallback
import kotlinx.android.synthetic.main.quote_item_c.view.*
import java.text.SimpleDateFormat
import java.util.*

class UpdatesAdapter(private val listener: Listener) : RecyclerView.Adapter<UpdatesAdapter.ViewHolder>() {
    interface Listener {
        fun confirm(demandId: String)
        fun deny(demandId: String)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var quotes = listOf<Demand>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.quote_item_c, parent, false)
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
            tx_date.text = sdf.format(netDate)
            tx_technqiue.setImageResource(when (quote.technique) {
                "laser" -> R.drawable.laser_small
                "impression" -> R.drawable.small_3d
                else -> R.drawable.frais_small
            }
            )
            tvPrice.text = "${quote.priceToClient} $"
            tvType.text = quote.type
            imTech.setImageResource(when (quote.technique) {
                "laser" -> R.drawable.bg_laser
                "impression" -> R.drawable.bg_3d
                else -> R.drawable.bg_fraisage
            }
            )

            ic_confirm.setOnClickListener { listener.confirm(quote._id) }
            ic_deny.setOnClickListener { listener.deny(quote._id) }


        }


    }


}