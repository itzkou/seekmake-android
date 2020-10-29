package com.kou.seekmake.screens.profile.Fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.kou.seekmake.R
import com.kou.seekmake.models.SeekMake.Demand
import com.kou.seekmake.models.SeekMake.OrderStatus
import com.kou.seekmake.screens.common.SharedUtils.PrefsManager
import com.kou.seekmake.screens.profile.Adapters.UpdatesAdapter
import com.kou.seekmake.screens.profile.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_quotes.*

class Updates : Fragment(), UpdatesAdapter.Listener {
    private lateinit var mAdapter: UpdatesAdapter
    private lateinit var viewModel: ProfileViewModel

    companion object {

        fun newInstance(): Updates = Updates()
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
                        if (demand.status == "verified")
                            arrStatus.add(demand)
                    }

                    mAdapter.updateDemands(arrStatus)
                    if (arrStatus.isEmpty()) {
                        animVerified.visibility = View.VISIBLE
                        tvVerif.visibility = View.VISIBLE
                    }
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
        mAdapter = UpdatesAdapter(this)
        rvQuotes.layoutManager = LinearLayoutManager(activity)
        rvQuotes.adapter = mAdapter
        return v

    }

    override fun confirm(demandId: String) {
        viewModel.updateDemand(demandId, PrefsManager.geToken(requireActivity())!!, OrderStatus("acceptedClient")).observe(this, Observer {
            if (it.msg == "0")
                Toast.makeText(requireActivity(), "Network faillure", Toast.LENGTH_SHORT).show()
            else if (it.msg == "OK")
                Toast.makeText(requireActivity(), "Quote confirmed", Toast.LENGTH_SHORT).show()


        })
    }

    override fun deny(demandId: String) {
        viewModel.updateDemand(demandId, PrefsManager.geToken(requireActivity())!!, OrderStatus("declined")).observe(this, Observer {
            if (it.msg == "0")
                Toast.makeText(requireActivity(), "Network faillure", Toast.LENGTH_SHORT).show()
            else if (it.msg == "OK")
                Toast.makeText(requireActivity(), "Quote canceled", Toast.LENGTH_SHORT).show()


        })
    }


}