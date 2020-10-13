package com.kou.seekmake.screens.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kou.seekmake.R
import com.kou.seekmake.models.SeekMake.Material


class CustomAdapter(mContext: Context, private val mData: ArrayList<Material>) : BaseAdapter() {
    private val mInflate: LayoutInflater = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        var view = convertView

        if (view == null) {
            view = mInflate.inflate(R.layout.item_spinner, parent, false)
            viewHolder = ViewHolder(view)
        } else {
            viewHolder = view.tag as ViewHolder
        }

        view?.tag = viewHolder

        viewHolder.tvUsername.text = mData[position].text

        return view!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: DropdownViewHolder
        var view = convertView

        if (view == null) {
            view = mInflate.inflate(R.layout.item_spinner, parent, false)
            viewHolder = DropdownViewHolder(view)
        } else {
            viewHolder = view.tag as DropdownViewHolder
        }

        view?.tag = viewHolder

        viewHolder.text.text = mData[position].text
        viewHolder.image.setImageResource(mData[position].image)

        return view!!
    }

    override fun getItem(position: Int): Any = mData[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = mData.size

    class ViewHolder(view: View) {
        lateinit var tvUsername: TextView

        init {
            tvUsername = view.findViewById(R.id.tx_spinner)
        }
    }

    class DropdownViewHolder(view: View) {
        var text: TextView = view.findViewById(R.id.tx_spinner)
        lateinit var image: ImageView

        init {
            image = view.findViewById(R.id.im_spinner)
        }
    }
}
