package com.colearn.unsplashtask.ui.landingpage

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.colearn.unsplashtask.MainApplication
import com.colearn.unsplashtask.R
import com.colearn.unsplashtask.pojos.ApiStatus
import com.colearn.unsplashtask.ui.common.PhotoListFragment
import com.colearn.unsplashtask.ui.common.PhotoListFragmentCallback
import com.colearn.unsplashtask.ui.searchpage.SearchPageFragment
import com.colearn.unsplashtask.ui.searchpage.SearchPageFragment.Companion.QUERY
import com.google.android.material.textfield.TextInputEditText

class LandingPageFragment : Fragment(), PhotoListFragmentCallback {

    companion object {
        fun newInstance() = LandingPageFragment()
    }

    private lateinit var viewModel: LandingPageViewModel
    private var photoListFragment: PhotoListFragment? = null
    private lateinit var searchField: TextInputEditText
    private lateinit var progressBarLayout: ViewGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.landing_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarLayout = view.findViewById<ViewGroup>(R.id.progressBarLayout)
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.app_name)
        searchField = view.findViewById<TextInputEditText>(R.id.textInputEditText)
        view.findViewById<ImageButton>(R.id.search_icon).setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(searchField.text)) {
                Toast.makeText(context, "Search query cannot be empty.", Toast.LENGTH_LONG).show()
            } else {
                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.container, SearchPageFragment.Companion.newInstance(Bundle().apply { putString(QUERY, searchField.text.toString()) }))
                        ?.addToBackStack(SearchPageFragment::class.java.name)
                        ?.commit()
            }
        })

        if(photoListFragment == null) {
            photoListFragment = PhotoListFragment.newInstance()
            photoListFragment?.photoListFragmentCallback = this
            childFragmentManager.beginTransaction().apply {
                replace(R.id.photolist, photoListFragment!!, PhotoListFragment::class::java.name)
                commit()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LandingPageViewModel::class.java)
        if (activity?.application is MainApplication) {
            val mainApplication: MainApplication = activity?.application as MainApplication
            if (viewModel.collectionApiLiveData.value == null) {
                viewModel.fetchLandingPagePhotos()
            }
            viewModel.collectionApiLiveData.observe(this, Observer { value ->
                activity?.apply {
                    runOnUiThread {
                        value?.data?.let { photoListFragment?.setPhotoList(it) }
                    }
                }
                progressBarLayout.visibility = if (value.apiStatus == ApiStatus.LOADING) View.VISIBLE else View.GONE
                if (value.apiStatus == ApiStatus.FAILURE) {
                    Toast.makeText(context, "Something went wrong, please try again.", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.collectionApiLiveData.removeObservers(this)
    }

    override fun loadMorePhotos() {
        if (activity?.application is MainApplication) {
            val mainApplication: MainApplication = activity?.application as MainApplication
            viewModel.fetchLandingPagePhotos()
        }
    }

}