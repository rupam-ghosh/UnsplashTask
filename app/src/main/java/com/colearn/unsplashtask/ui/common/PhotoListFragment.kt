package com.colearn.unsplashtask.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.colearn.unsplashtask.R
import com.colearn.unsplashtask.pojos.Photo
import com.colearn.unsplashtask.ui.fullscreenimage.FullScreenImageFragment
import java.util.*


/**
 * A fragment representing a list of Items.
 */
interface PhotoListFragmentCallback {
    fun loadMorePhotos()
}

class PhotoListFragment : Fragment(), View.OnClickListener {

    private var photoAdapter: MyPhotoListRecyclerViewAdapter? = null
    private var photoList: ArrayList<Photo> = ArrayList<Photo>()
    var photoListFragmentCallback: PhotoListFragmentCallback? = null
    var lastPageItemCount = 0
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        val clickListener = this
        if (view is RecyclerView) {
            recyclerView = view
            with(view) {
                photoAdapter = MyPhotoListRecyclerViewAdapter(context, clickListener, photoList)
                val staggeredLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                layoutManager = staggeredLayoutManager
                adapter = photoAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (!recyclerView.canScrollVertically(1) && photoList.size != lastPageItemCount) {
                            photoListFragmentCallback?.loadMorePhotos()
                            lastPageItemCount = photoList.size
                        }
                        (recyclerView.layoutManager as StaggeredGridLayoutManager?)?.invalidateSpanAssignments()
                    }
                })
            }
        }
        return view
    }

    fun setPhotoList(value: ArrayList<Photo>) {
        photoAdapter?.setPhotoList(value)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                PhotoListFragment()
    }

    override fun onClick(v: View?) {
        v?.let {
            val position = it.tag as Int
            position.let {
                val photo = photoList.get(it)
                photo.urls?.full?.let {
                    val url: String = it
                    photo.id?.let {
                        activity?.supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.container, FullScreenImageFragment.Companion.newInstance(url, it))
                                ?.addToBackStack(FullScreenImageFragment::class.java.name)
                                ?.commit()
                    }
                }
            }
        }
    }
}