package com.colearn.unsplashtask.ui.fullscreenimage

import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.colearn.unsplashtask.R
import com.colearn.unsplashtask.async.DownloadImage
import com.colearn.unsplashtask.async.DownloadImageInterface

/**
 * A simple [Fragment] subclass.
 * Use the [FullScreenImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FullScreenImageFragment : Fragment(), DownloadImageInterface {
    private var imageUrl: String? = null
    private var imageId: String? = null
    private lateinit var progressBar: ProgressBar
    private var downloadedDrawable: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString(IMAGE_URL)
            imageId = it.getString(IMAGE_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full_screen_image, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progressBar)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.inflateMenu(R.menu.full_screen)
        toolbar.navigationIcon = resources.getDrawable(android.R.drawable.ic_menu_close_clear_cancel)
        toolbar.setNavigationOnClickListener { activity?.supportFragmentManager?.popBackStackImmediate() }
        toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.download) {
                downloadedDrawable?.let {
                    val drawable = it
                    imageId?.let {
                        DownloadImage(drawable.toBitmap(), it, this).execute()
                    }
                }
            }
            false
        })
        context?.let {
            Glide.with(it)
                    .load(imageUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            downloadedDrawable = resource
                            progressBar.visibility = View.GONE
                            return false
                        }

                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            progressBar.visibility = View.GONE
                            Toast.makeText(context, "Image load failed. Please try again", Toast.LENGTH_LONG).show()
                            return false
                        }
                    })
                    .dontAnimate()
                    .into(view.findViewById(R.id.imageView))
        }
    }

    override fun galleryAddPic(file: String) {
        MediaScannerConnection.scanFile(context, arrayOf(file),
                null, MediaScannerConnection.OnScanCompletedListener { path, uri ->
                Toast.makeText(context, if(uri != null) "File Scan completed" else "File Scan failed", Toast.LENGTH_SHORT).show()
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(imageUrl: String, imageId: String) =
                FullScreenImageFragment().apply {
                    arguments = Bundle().apply {
                        putString(IMAGE_URL, imageUrl)
                        putString(IMAGE_ID, imageId)
                    }
                }

        const val IMAGE_URL = "image_url"
        const val IMAGE_ID = "image_id"
    }
}