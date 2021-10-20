package com.colearn.unsplashtask.ui.searchpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.colearn.unsplashtask.MainApplication
import com.colearn.unsplashtask.R
import com.colearn.unsplashtask.pojos.ApiStatus
import com.colearn.unsplashtask.ui.advancedsearchpage.AdvancedSearchPageFragment
import com.colearn.unsplashtask.ui.advancedsearchpage.AdvancedSearchPageFragmentCallback
import com.colearn.unsplashtask.ui.common.PhotoListFragment
import com.colearn.unsplashtask.ui.common.PhotoListFragmentCallback

class SearchPageFragment : Fragment(), PhotoListFragmentCallback,
        AdvancedSearchPageFragmentCallback {

    companion object {
        @JvmStatic
        val QUERY = "query"

        @JvmStatic
        fun newInstance(bundle: Bundle) = SearchPageFragment().apply { arguments = bundle }
    }

    private lateinit var viewModel: SearchPageViewModel
    private lateinit var textView: TextView
    private var photoListFragment: PhotoListFragment? = null
    private lateinit var progressBarLayout: ViewGroup
    private var orderBy: String? = null
    private var options: MutableMap<String, String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView = view.findViewById<TextView>(R.id.textView)
        progressBarLayout = view.findViewById<ViewGroup>(R.id.progressBarLayout)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.navigationIcon = resources.getDrawable(android.R.drawable.ic_menu_close_clear_cancel)
        toolbar.inflateMenu(R.menu.search_menu)
        val callback: AdvancedSearchPageFragmentCallback = this
        toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.advancedsearch) {
                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(
                                R.id.container,
                                AdvancedSearchPageFragment.Companion.newInstance(callback, orderBy, options)
                        )
                        ?.addToBackStack(AdvancedSearchPageFragment::class.java.name)
                        ?.commit()
            }
            false
        })
        toolbar.setNavigationOnClickListener { activity?.supportFragmentManager?.popBackStackImmediate() }
        toolbar.title = arguments?.getString(QUERY)

        // load PhotoListFragment in the photo list container
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
        viewModel = ViewModelProvider(this).get(SearchPageViewModel::class.java)
        if (activity?.application is MainApplication) {
            val mainApplication: MainApplication = activity?.application as MainApplication
            val query = arguments?.getString(QUERY)
            query?.let {
                if (viewModel.searchApiLiveData.value == null) {
                    viewModel.initiateSearch(it)
                }
                viewModel.searchApiLiveData.observe(this, Observer { value ->
                    value.data?.results?.let {
                        if (it.size > 0) {
                            textView.visibility = View.INVISIBLE
                        } else {
                            textView.visibility = View.VISIBLE
                        }
                        activity?.apply {
                            runOnUiThread {
                                photoListFragment?.setPhotoList(it)
                            }
                        }
                    }
                    textView.visibility = if (value.data?.results != null) View.VISIBLE else View.INVISIBLE
                    progressBarLayout.visibility = if (value.apiStatus == ApiStatus.LOADING) View.VISIBLE else View.GONE
                    when(value.apiStatus) {
                        ApiStatus.FAILURE -> Toast.makeText(context, "Something went wrong, please try again.", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.searchApiLiveData.removeObservers(this)
    }

    override fun loadMorePhotos() {
        if (activity?.application is MainApplication) {
            val mainApplication: MainApplication = activity?.application as MainApplication
            val query = arguments?.getString(QUERY)
            query?.let {
                if (this.options != null) {
                    viewModel.advancedSearch(it, this.orderBy!!, this.options!!)
                } else {
                    viewModel.initiateSearch(it)
                }
            }
        }
    }

    override fun clear() {
        this.orderBy = null
        this.options = null
        if (activity?.application is MainApplication) {
            val mainApplication: MainApplication = activity?.application as MainApplication
            val query = arguments?.getString(QUERY)
            query?.let {
                viewModel.clear()
                viewModel.initiateSearch(it)
            }
        }
    }

    override fun apply(orderBy: String, options: MutableMap<String, String>) {
        this.orderBy = orderBy
        this.options = options
        if (activity?.application is MainApplication) {
            val mainApplication: MainApplication = activity?.application as MainApplication
            val query = arguments?.getString(QUERY)
            query?.let {
                viewModel.clear()
                viewModel.advancedSearch(it, orderBy, options)
            }
        }
    }
}