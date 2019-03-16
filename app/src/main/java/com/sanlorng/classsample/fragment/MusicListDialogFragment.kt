package com.sanlorng.classsample.fragment

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.sanlorng.classsample.R
import com.sanlorng.classsample.model.MusicModel
import kotlinx.android.synthetic.main.fragment_item_list_dialog.*
import kotlinx.android.synthetic.main.fragment_item_list_dialog_item.view.*

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    MusicListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [MusicListDialogFragment.Listener].
 */
class MusicListDialogFragment(private val listMusic : ArrayList<MusicModel>) : BottomSheetDialogFragment() {
    private var mListener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = ItemAdapter(listMusic)
        list.minimumHeight = resources.displayMetrics.heightPixels /2
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as Listener
        } else {
            mListener = context as Listener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onItemClicked(list:ArrayList<MusicModel>,position: Int)
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_item_list_dialog_item, parent, false)) {

    }

    private inner class ItemAdapter internal constructor(private val list: ArrayList<MusicModel>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            listMusic[position].run {
                holder.itemView.apply {
                    textMusicTitle.text = title
                    textMusicAlbum.text = String.format("%s - %s",artist,album)
                    setOnClickListener {
                        mListener?.onItemClicked(listMusic,position)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    companion object {

        // TODO: Customize parameters
        fun newInstance(listMusic: ArrayList<MusicModel>): MusicListDialogFragment =
            MusicListDialogFragment(listMusic).apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, listMusic.size)
                }
            }

    }
}
