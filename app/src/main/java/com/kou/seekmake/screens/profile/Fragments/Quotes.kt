package com.kou.seekmake.screens.profile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.kou.seekmake.R
import com.kou.seekmake.models.SeekMake.Demand
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
        viewModel.getDemands(PrefsManager.geToken(requireActivity())!!, PrefsManager.geID(requireActivity())!!
        ).observe(requireActivity(), Observer {
            it?.let { demandsResponse ->
                if (demandsResponse.data != null) {
                    val arrStatus = arrayListOf<Demand>()
                    demandsResponse.data.forEach { demand ->
                        //if (demand.status == "verified")
                        arrStatus.add(demand)
                    }

                    mAdapter.updateDemands(arrStatus)
                }

            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_quotes, container, false)
        val rvQuotes = v.findViewById<RecyclerView>(R.id.quotes_recycler)
        val animVerif = v.findViewById<LottieAnimationView>(R.id.animVerified)
        val tvVerif = v.findViewById<TextView>(R.id.tvVerif)
        animVerif.visibility = View.GONE
        tvVerif.visibility = View.GONE
        mAdapter = QuotesAdapter(this)
        rvQuotes.layoutManager = LinearLayoutManager(activity)
        rvQuotes.adapter = mAdapter
        return v

    }


    override fun updateDemand(demandId: String) {
    }


}