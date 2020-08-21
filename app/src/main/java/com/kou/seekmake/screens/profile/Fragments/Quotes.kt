package com.kou.seekmake.screens.profile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kou.seekmake.R
import com.kou.seekmake.screens.common.SharedUtils.PrefsManager
import com.kou.seekmake.screens.profile.Adapters.QuotesAdapter
import com.kou.seekmake.screens.profile.ProfileViewModel

class Quotes : Fragment(), QuotesAdapter.Listener {
    private lateinit var mAdapter: QuotesAdapter
    private lateinit var viewModel: ProfileViewModel

    companion object {

        fun newInstance(): Quotes = Quotes()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(requireActivity()).get<ProfileViewModel>(ProfileViewModel::class.java)
        viewModel.getDemands(PrefsManager.geID(requireActivity())!!
        ).observe(requireActivity(), Observer {
            it?.let { demandsResponse ->
                mAdapter.updatePosts(demandsResponse.data!!)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_quotes, container, false)
        val rvQuotes = v.findViewById<RecyclerView>(R.id.quotes_recycler)
        mAdapter = QuotesAdapter(this)
        rvQuotes.layoutManager = LinearLayoutManager(activity)
        rvQuotes.adapter = mAdapter
        return v

    }

    override fun confirm(postId: String) {
        TODO("Not yet implemented")
    }

    override fun deny(postId: String, position: Int) {
        TODO("Not yet implemented")
    }


}