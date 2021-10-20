package com.colearn.unsplashtask.ui.advancedsearchpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.colearn.unsplashtask.R

/**
 * A simple [Fragment] subclass.
 * Use the [AdvancedSearchPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
interface AdvancedSearchPageFragmentCallback {
    fun clear()
    fun apply(orderBy: String, options: MutableMap<String, String>)
}

class AdvancedSearchPageFragment : Fragment() {

    private lateinit var callback: AdvancedSearchPageFragmentCallback
    private var orderBy: String? = null
    private var options: MutableMap<String, String>? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_advanced_search_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Filter"
        toolbar.inflateMenu(R.menu.advanced_search)
        toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.cancel) {
                activity?.supportFragmentManager?.popBackStackImmediate()
            }
            false
        })
        val clearButton = view.findViewById<Button>(R.id.clear_button)
        clearButton.setOnClickListener({
            activity?.supportFragmentManager?.popBackStackImmediate()
            callback.clear()
        })
        val orderBy: String = if (options?.get("order_by") == null) "relevant" else options!!.get("order_by")!!
        val color: String = if (options?.get("color") == null) "any_color" else options!!.get("color")!!
        val orientation: String = if (options?.get("orientation") == null) "any_orientation" else options!!.get("orientation")!!
        val applyButton = view.findViewById<Button>(R.id.apply_button)
        val sortRadioGroup = view.findViewById<RadioGroup>(R.id.sort_group)
        sortRadioGroup.check(getResourceFromData(orderBy))
        val colorRadioGroup = view.findViewById<RadioGroup>(R.id.color_group)
        colorRadioGroup.check(getResourceFromData(color))
        val orientationRadioGroup = view.findViewById<RadioGroup>(R.id.orientation_group)
        orientationRadioGroup.check(getResourceFromData(orientation))
        applyButton.setOnClickListener({
            activity?.supportFragmentManager?.popBackStackImmediate()
            val dataOptions: MutableMap<String, String> = mutableMapOf()
            val colorData = getDataFromResource(colorRadioGroup.checkedRadioButtonId)
            val orientationData = getDataFromResource(orientationRadioGroup.checkedRadioButtonId)
            colorData?.let { dataOptions.put("color", it) }
            orientationData?.let { dataOptions.put("orientation", it) }
            getDataFromResource(sortRadioGroup.checkedRadioButtonId)?.let {
                callback.apply(it, dataOptions)
            }
        })
    }

    fun getResourceFromData(data: String): Int {
        when (data) {
            "relevant" -> {
                return R.id.relevance
            }
            "latest" -> {
                return R.id.newest
            }
            "any_color" -> {
                return R.id.any_color
            }
            "black_and_white" -> {
                return R.id.black_and_white
            }
            "any_orientation" -> {
                return R.id.any_orientation
            }
            "portrait" -> {
                return R.id.portrait
            }
            "landscape" -> {
                return R.id.landscape
            }
            "squarish" -> {
                return R.id.square
            }
        }
        return -1
    }

    fun getDataFromResource(id: Int): String? {
        when (id) {
            R.id.relevance -> {
                return "relevant"
            }
            R.id.newest -> {
                return "latest"
            }
            R.id.any_color -> {
                return null
            }
            R.id.black_and_white -> {
                return "black_and_white"
            }
            R.id.any_orientation -> {
                return null
            }
            R.id.portrait -> {
                return "portrait"
            }
            R.id.landscape -> {
                return "landscape"
            }
            R.id.square -> {
                return "squarish"
            }
        }
        return null
    }

    companion object {
        @JvmStatic
        fun newInstance(mCallback: AdvancedSearchPageFragmentCallback, mOrderBy: String?, mOptions: MutableMap<String, String>?) =
                AdvancedSearchPageFragment().apply {
                    callback = mCallback
                    options = mOptions
                    orderBy = mOrderBy
                }
    }
}