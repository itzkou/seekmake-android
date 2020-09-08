package com.kou.seekmake.screens.profile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kou.seekmake.R
import com.kou.seekmake.screens.common.ImagesAdapter
import com.kou.seekmake.screens.profile.ProfileViewModel


class Images : Fragment(), ImagesAdapter.Listener {
    private lateinit var mAdapter: ImagesAdapter
    private lateinit var viewModel: ProfileViewModel

    companion object {

        fun newInstance(): Images = Images()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(requireActivity()).get<ProfileViewModel>(ProfileViewModel::class.java)
        viewModel.images.observe(requireActivity(), Observer {
            it?.let { images ->
                mAdapter.updateImages(images)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_images, container, false)
        val imagesRecycler = v.findViewById<RecyclerView>(R.id.images_recycler)
        mAdapter = ImagesAdapter(this)
        imagesRecycler.layoutManager = GridLayoutManager(activity, 3)

        imagesRecycler.adapter = mAdapter
        return v

    }

    override fun managePost() {
    }


}